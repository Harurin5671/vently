package com.app.crowns.common.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

object ImageUtils {
    /**
     * Comprime una imagen desde una Uri a un ByteArray.
     * Redimensiona la imagen si es muy grande y aplica compresión JPEG.
     */
    fun compressImage(
        context: Context,
        uri: Uri,
        maxWidth: Int = 800,
        maxHeight: Int = 800,
        quality: Int = 80
    ): ByteArray? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) return null

            // Calcular nuevas dimensiones manteniendo el aspect ratio
            val ratio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
            var newWidth = originalBitmap.width
            var newHeight = originalBitmap.height

            if (newWidth > maxWidth) {
                newWidth = maxWidth
                newHeight = (newWidth / ratio).toInt()
            }

            if (newHeight > maxHeight) {
                newHeight = maxHeight
                newWidth = (newHeight * ratio).toInt()
            }

            // Redimensionar
            val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
            
            // Comprimir
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            
            // Limpiar memoria
            if (resizedBitmap != originalBitmap) {
                resizedBitmap.recycle()
            }
            originalBitmap.recycle()

            outputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
