package com.app.crowns.data.invoice.mapper

import com.app.crowns.data.invoice.model.SendToSunatRequestModel
import com.app.crowns.data.invoice.model.SendToSunatResponseModel
import com.app.crowns.domain.invoice.entities.SendToSunatRequestEntity
import com.app.crowns.domain.invoice.entities.SunatResponseEntity

fun SendToSunatRequestEntity.toModel(): SendToSunatRequestModel = SendToSunatRequestModel(
    zipBase64 = zipBase64,
    fileName = fileName,
    ruc = ruc,
    user = user,
    clave = clave
)

fun SendToSunatResponseModel.toDomain(): SunatResponseEntity = SunatResponseEntity(
    code = code,
    description = description
)
