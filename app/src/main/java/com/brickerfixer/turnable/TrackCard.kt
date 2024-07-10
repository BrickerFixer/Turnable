package com.brickerfixer.turnable

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.ktx.Status

@OptIn(ExperimentalGlideComposeApi::class)
@SuppressLint("SimpleDateFormat")
@Composable
fun TrackCard(title: String?, artist: String?, imageUri: Uri?){
    ElevatedCard(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)) {
        Row (verticalAlignment = Alignment.CenterVertically) {
            GlideImage(model = imageUri, contentDescription = "Artwork", modifier = Modifier.size(128.dp))
            Column (modifier = Modifier.padding(8.dp)) {
                title?.let { Text(text = it, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall) }
                artist?.let { Text(text = it) }
            }
        }
    }
}