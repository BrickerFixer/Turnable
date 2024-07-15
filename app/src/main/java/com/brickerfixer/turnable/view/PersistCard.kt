package com.brickerfixer.turnable.view

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.brickerfixer.turnable.viewmodel.PlayerViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@SuppressLint("SimpleDateFormat")
@Composable
fun PersistCard(title: String?, artist: String?, imageUri: Uri?, playerViewModel: PlayerViewModel){
    ElevatedCard(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface), onClick = {
            if (title != null) {
                playerViewModel.addMediaItem(title)
                playerViewModel.play()
            }
        }) {
        Column (horizontalAlignment = Alignment.CenterHorizontally) {
            GlideImage(model = imageUri, contentDescription = "Artwork", modifier = Modifier.size(128.dp))
            title?.let { Text(text = it, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis) }
            artist?.let { Text(text = it) }
        } //TODO: Save metadata
    }
}