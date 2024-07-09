package com.brickerfixer.turnable

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.brickerfixer.turnable.ui.theme.TurnableTheme
import com.google.common.util.concurrent.MoreExecutors
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

    }

    @OptIn(UnstableApi::class)
    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, ExoplayerService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                // Call controllerFuture.get() to retrieve the MediaController.
                // MediaController implements the Player interface, so it can be
                // attached to the PlayerView UI component.
                setContent {
                    TurnableTheme {
                        Scaffold(modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding()) { innerPadding ->
                            Player(
                                modifier = Modifier.padding(innerPadding), player = controllerFuture.get()
                            )
                        }
                    }
                }
            },
            MoreExecutors.directExecutor()
        )
    }

}


@Composable
fun Player(modifier: Modifier = Modifier, player: Player?) {
    var text by remember { mutableStateOf("") }
    Column {
        FloatingActionButton(onClick = { player?.play() }) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play")
        }
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("track URI") }
        )
        Button(onClick = { player?.addMediaItem(MediaItem.fromUri(text)) }) {
            Text(text = "Add")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TurnableTheme {
        Player(player = null)
    }
}