package com.app.crowns.data.invoice.datasource

import android.util.Log
import com.app.crowns.data.invoice.dto.GenerateXmlRequestDto
import com.app.crowns.data.invoice.model.CancelInvoiceResponseModel
import com.app.crowns.domain.catalog.entities.InvoiceStatusEntity
import com.app.crowns.data.invoice.model.InvoiceItemSupabaseModel
import com.app.crowns.data.invoice.model.InvoiceSupabaseModel
import com.app.crowns.data.invoice.model.InvoiceXmlResponseModel
import com.app.crowns.data.invoice.model.SendSunatInvoiceSupabaseModel
import com.app.crowns.data.invoice.model.SignInvoiceSupabaseModel
import com.app.crowns.network.client.HttpClient
import com.app.crowns.network.di.ApiBaseUrl
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import javax.inject.Inject

class InvoiceSupabaseDataSourceImpl @Inject constructor(
    private val supabase: SupabaseClient,
    private val httpClient: HttpClient,
    @param:ApiBaseUrl private val baseUrl: String,
) : InvoiceSupabaseDataSource {
    override suspend fun getInvoicesHistory(
        companyId: String,
        startDate: String,
        endDate: String,
        offset: Int,
        limit: Int,
    ): Result<List<InvoiceSupabaseModel>> = runCatching {
        Log.d("HISTORY_DS", "Query: companyId=$companyId start=$startDate end=$endDate offset=$offset")
        val result = supabase
            .from("invoices")
            .select(Columns.raw("*, invoice_items(*)")) {
                filter {
                    and {
                        eq("company_id", companyId)
                        gte("issue_date", startDate)
                        lte("issue_date", endDate)
                    }
                }
                order("issue_date", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                range(offset.toLong(), (offset + limit - 1).toLong())
            }
            .decodeList<InvoiceSupabaseModel>()
        Log.d("HISTORY_DS", "Resultado: ${result.size} facturas. Fechas: ${result.map { it.issueDate }}")
        result
    }

    override suspend fun getInvoiceItems(invoiceId: String): Result<List<InvoiceItemSupabaseModel>> =
        runCatching {
            supabase
                .from("invoice_items")
                .select {
                    filter { eq("invoice_id", invoiceId) }
                }
                .decodeList<InvoiceItemSupabaseModel>()
        }

    override suspend fun generateXml(request: GenerateXmlRequestDto): Result<InvoiceXmlResponseModel> =
        runCatching {
            val response = supabase.functions.invoke(
                function = "generate-xml",
                body = request
            )
            response.body<InvoiceXmlResponseModel>()
        }

    override suspend fun signInvoice(invoiceId: String): Result<SignInvoiceSupabaseModel> = runCatching {
        supabase.functions.invoke(
            function = "sign-invoice",
            body = mapOf("invoice_id" to invoiceId)
        ).body<SignInvoiceSupabaseModel>()
    }

    override suspend fun sendToSunat(invoiceId: String): Result<SendSunatInvoiceSupabaseModel> = runCatching {
        supabase.functions.invoke(
            function = "send-sunat",
            body = mapOf("invoice_id" to invoiceId)
        ).body<SendSunatInvoiceSupabaseModel>()
    }

    override suspend fun cancelInvoice(invoiceId: String): Result<Unit> = runCatching {
        supabase.from("invoices").update({ set("status", InvoiceStatusEntity.CANCELLED) }) {
            filter { eq("id", invoiceId) }
        }
    }

    override suspend fun cancelAcceptedInvoice(invoiceId: String, reason: String): Result<CancelInvoiceResponseModel> =
        runCatching {
            supabase.functions.invoke(
                function = "cancel-invoice",
                body = mapOf("invoice_id" to invoiceId, "reason" to reason)
            ).body<CancelInvoiceResponseModel>()
        }
}
