package com.brickerfixer.turnable

import android.annotation.SuppressLint
import android.content.ComponentName
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Source
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
                        Main(
                            player = controllerFuture.get()
                        )
                    }
                }
            },
            MoreExecutors.directExecutor()
        )
    }
}

/*
Three whales that manage app displaying and navigation.
One is God. God holds the important role of keeping navigation together.
*/

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(player: Player?) {
    val navController = rememberNavController()
    Scaffold(bottomBar = {BottomNavigationBar(navController = navController)}) { innerPadding ->
        NavHost(navController, startDestination = NavRoutes.Player.route, modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            composable(NavRoutes.Player.route) { Player(player = player) }
            composable(NavRoutes.Queue.route) { Queue(player = player)  }
            composable(NavRoutes.Sources.route) { Sources(player = player) }
            composable(NavRoutes.Settings.route) { Settings(player = player) }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        NavBarItems.BarItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.findStartDestination().id) {saveState = true}
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(imageVector = navItem.image,
                        contentDescription = navItem.title)
                },
                label = {
                    Text(text = navItem.title)
                }
            )
        }
    }
}

object NavBarItems {
    val BarItems = listOf(
        BarItem(
            title = "Player",
            image = Icons.Rounded.PlayCircle,
            route = "player"
        ),
        BarItem(
            title = "Queue",
            image = Icons.AutoMirrored.Rounded.QueueMusic,
            route = "queue"
        ),
        BarItem(
            title = "Sources",
            image = Icons.Rounded.Source,
            route = "sources"
        ),
        BarItem(
            title = "Settings",
            image = Icons.Rounded.Settings,
            route = "settings"
    )
    )
}

data class BarItem(
    val title: String,
    val image: ImageVector,
    val route: String
)

sealed class NavRoutes(val route: String) {
    object Player : NavRoutes("player")
    object Queue : NavRoutes("queue")
    object Sources : NavRoutes("sources")
    object Settings : NavRoutes("settings")
}

@kotlin.OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Player(modifier: Modifier = Modifier, player: Player?) {
    var isPlaying by remember { mutableStateOf(player?.isPlaying) }
    var currentTrack by remember { mutableStateOf(player?.mediaMetadata?.title) }
    var currentArtist by remember { mutableStateOf(player?.mediaMetadata?.artist) }
    var albumCover by remember { mutableStateOf(player?.mediaMetadata?.artworkUri) }
    Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Card(onClick = { /*TODO*/ }, modifier = Modifier.size(270.dp)) {

        }
        Column (horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = currentTrack.toString(), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text(text = currentArtist.toString(), Modifier.alpha(0.5f))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {  }) {
                Icon(painter = painterResource(id = R.drawable.repeat_24px), contentDescription = "Repeat")
            }
            IconButton(onClick = { player?.seekToPreviousMediaItem() }) {
                Icon(painter = painterResource(id = R.drawable.skip_previous_24px), contentDescription = "Previous")
            }
            FloatingActionButton(onClick = { togglePlayback(player) }) {
                if (isPlaying == true){
                    Icon(painter = painterResource(id = R.drawable.pause_24px), contentDescription = "Play")
                } else {
                    Icon(painter = painterResource(id = R.drawable.play_arrow_24px), contentDescription = "Play")
                }
            }
            IconButton(onClick = { player?.seekToNextMediaItem() }) {
                Icon(painter = painterResource(id = R.drawable.skip_next_24px), contentDescription = "Previous")
            }
            IconButton(onClick = { player?.shuffleModeEnabled = !player?.shuffleModeEnabled!! }) {
                if (player?.shuffleModeEnabled == true){
                    Icon(painter = painterResource(id = R.drawable.shuffle_on_24px), contentDescription = "Shuffle On")
                } else {
                    Icon(painter = painterResource(id = R.drawable.shuffle_24px), contentDescription = "Shuffle Off")
                }
            }
        }
    }
}

@Composable
fun Sources(player: Player?) {
    Column (modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()) {
        Row (modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { /*TODO*/ }, modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(end = 4.dp)) {
                Text(text = "Scan network")
            }
            Button(onClick = { /*TODO*/ }, modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp)) {
                Text(text = "Local storage")
            }
        }
        Column (modifier = Modifier
            .fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
            var text by remember { mutableStateOf("") }
            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)) {
                // Add 5 items
                items(20) { index ->
                    Text(text = "TRACK $index")
                }
            }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("track URI") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Button(onClick = { player?.addMediaItem(MediaItem.fromUri(text)) }, modifier = Modifier.fillMaxWidth()) {
                Icon(painter = painterResource(id = R.drawable.add_circle_24px), contentDescription = "Add")
                Text(text = "Add")
            }
        }
    }
}

@Composable
fun Settings(player: Player?) {
    Column (modifier = Modifier.fillMaxSize()) {

    }
}

@Composable
fun Queue(player: Player?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (player?.mediaItemCount == 0){
            Text(text = "Queue is empty!")
        }
        LazyColumn (modifier = Modifier.fillMaxSize()) {
            player?.mediaItemCount?.let {
                items(it){  index ->
                    TrackCard(title = player.getMediaItemAt(index).mediaMetadata.title.toString(), artist = player.getMediaItemAt(index).mediaMetadata.artist.toString(), imageUri = player.getMediaItemAt(index).mediaMetadata.artworkUri)
                }
            }
        }
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Clear all")
        }
    }
}

// Utility functions

fun togglePlayback(player: Player?){
    when(player?.isPlaying){
        true -> player.pause()
        false -> player.play()
        null -> TODO()
    }
}
fun toggleRepeatStates(player: Player?) {
    
}
@Preview(showBackground = true, apiLevel = 34)
@Composable
fun GreetingPreview() {
    TurnableTheme {
        Main(player = null)
    }
}
