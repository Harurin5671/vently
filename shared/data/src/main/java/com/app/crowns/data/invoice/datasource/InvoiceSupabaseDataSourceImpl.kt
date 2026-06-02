package com.app.crowns.data.invoice.datasource

import android.util.Log
import com.app.crowns.data.invoice.dto.GenerateXmlRequestDto
import com.app.crowns.data.invoice.model.InvoiceItemSupabaseModel
import com.app.crowns.data.invoice.model.InvoiceSupabaseModel
import com.app.crowns.data.invoice.model.InvoiceXmlResponseModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.client.call.body
import javax.inject.Inject

class InvoiceSupabaseDataSourceImpl @Inject constructor(
    private val supabase: SupabaseClient
) : InvoiceSupabaseDataSource {
    override suspend fun getInvoicesHistory(companyId: String): Result<List<InvoiceSupabaseModel>> =
        runCatching {
            Log.d("INVOICE_DS", "Buscando facturas para companyId: $companyId")
            val result = supabase
                .from("invoices")
                .select(Columns.raw("*, invoice_items(*)")) {
                    filter { eq("company_id", companyId) }
                }
                .decodeList<InvoiceSupabaseModel>()
            Log.d("INVOICE_DS", "Facturas encontradas en Supabase: ${result.size}")
            if (result.isNotEmpty()) {
                Log.d("INVOICE_DS", "Primera factura: ${result.first()}")
            }
            result
        }.onFailure {
            Log.e("INVOICE_DS", "Error al traer historial de facturas", it)
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
            Log.d("Prueba_Generate_Xml", "DS: Iniciando invoke de la Edge Function 'generate-xml'")
            Log.d("Prueba_Generate_Xml", "DS: Body del request: $request")
            val response = supabase.functions.invoke(
                function = "generate-xml",
                body = request
            )
            Log.d("Prueba_Generate_Xml", "DS: Respuesta recibida de Supabase. Status: ${response.status}")
            val body = response.body<InvoiceXmlResponseModel>()
            Log.d("Prueba_Generate_Xml", "DS: Cuerpo decodificado exitosamente: $body")
            body
        }.onFailure {
            Log.e("Prueba_Generate_Xml", "DS: Error crítico en la llamada a la función", it)
        }
}
