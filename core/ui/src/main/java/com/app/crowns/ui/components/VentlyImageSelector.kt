package com.app.crowns.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.app.crowns.designsystem.color.Primary
import com.app.crowns.designsystem.color.PrimaryContainer

@Composable
fun VentlyImageSelector(
    selectedImageUri: Uri?,
    onImageSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    shape: androidx.compose.ui.graphics.Shape = CircleShape,
    existingImageUrl: String? = null,
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onImageSelected(it) }
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(PrimaryContainer.copy(alpha = 0.1f))
            .clickable {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        val imageModel: Any? = selectedImageUri ?: existingImageUrl
        if (imageModel != null) {
            AsyncImage(
                model = imageModel,
                contentDescription = "Selected Image",
                modifier = Modifier.clip(shape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Filled.AddAPhoto,
                contentDescription = "Select Image",
                tint = Primary,
                modifier = Modifier.size(size / 2)
            )
        }
    }
}
