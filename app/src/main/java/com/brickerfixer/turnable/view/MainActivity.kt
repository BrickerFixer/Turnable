package com.brickerfixer.turnable.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
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
import androidx.media3.common.Player
import com.brickerfixer.turnable.ui.theme.TurnableTheme
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Source
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Slider
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.brickerfixer.turnable.R
import com.brickerfixer.turnable.viewmodel.PlayerViewModel

class MainActivity : ComponentActivity() {
    private lateinit var playerViewModel: PlayerViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        playerViewModel = ViewModelProvider(this)[PlayerViewModel::class.java]
        playerViewModel.initializeMediaController(this) {
            setContent {
                TurnableTheme {
                    Main(playerViewModel)
                }
            }
        }
    }
}


@Composable
fun Main(playerModel: PlayerViewModel) {
    val navController = rememberNavController()
    Scaffold(bottomBar = { BottomNavigationBar(navController = navController) }) { innerPadding ->
        NavHost(navController, startDestination = NavRoutes.Player.route, modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            composable(NavRoutes.Player.route) { Player(playerModel = playerModel) }
            composable(NavRoutes.Queue.route) { Queue(playerModel = playerModel)  }
            composable(NavRoutes.Sources.route) { Sources(playerModel = playerModel) }
            composable(NavRoutes.Settings.route) { Settings() }
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
    data object Player : NavRoutes("player")
    data object Queue : NavRoutes("queue")
    data object Sources : NavRoutes("sources")
    data object Settings : NavRoutes("settings")
}

@Composable
fun Player(playerModel: PlayerViewModel) {
    val isPlaying by playerModel.isPlaying.observeAsState(false)
    val isShuffling by playerModel.shuffleModeEnabled.observeAsState(false)
    val repeatState by playerModel.repeatMode.observeAsState(Player.REPEAT_MODE_OFF)
    val currentTrack by playerModel.currentTrack.observeAsState("")
    val currentArtist by playerModel.currentArtist.observeAsState("")
    Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)) {
        Card(modifier = Modifier.size(270.dp)) {
            Image(painter = painterResource(id = R.drawable.artwork), contentDescription = "Artwork", modifier = Modifier.fillMaxSize())
        }
        Column (horizontalAlignment = Alignment.CenterHorizontally) {
            if (currentTrack != "" && currentArtist != ""){
                Text(text = currentTrack.toString(), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(text = currentArtist.toString(), Modifier.alpha(0.5f), textAlign = TextAlign.Center)
            } else {
                Text(text = stringResource(id = R.string.noplay), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(text = stringResource(id = R.string.hint), Modifier.alpha(0.5f), textAlign = TextAlign.Center)
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Slider(value = 0f, onValueChange = {})
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = "CURRENT_TIME")
                Text(text = "MEDIA_LENGTH")
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
            IconButton(onClick = { playerModel.toggleRepeat() }) {
                when(repeatState){
                    Player.REPEAT_MODE_OFF -> {
                        Icon(painter = painterResource(id = R.drawable.repeat_24px), contentDescription = "Repeat Off")
                    }

                    Player.REPEAT_MODE_ALL -> {
                        Icon(painter = painterResource(id = R.drawable.repeat_on_24px), contentDescription = "Repeat All")
                    }

                    Player.REPEAT_MODE_ONE -> {
                        Icon(painter = painterResource(id = R.drawable.repeat_one_on_24px), contentDescription = "Repeat One")
                    }
                }
            }
            IconButton(onClick = { playerModel.seekToPrevious() }) {
                Icon(painter = painterResource(id = R.drawable.skip_previous_24px), contentDescription = "Previous")
            }
            FloatingActionButton(onClick = { playerModel.togglePlayback() }) {
                if (isPlaying){
                    Icon(painter = painterResource(id = R.drawable.pause_24px), contentDescription = "Pause")
                } else {
                    Icon(painter = painterResource(id = R.drawable.play_arrow_24px), contentDescription = "Play")
                }
            }
            IconButton(onClick = { playerModel.seekToNext() }) {
                Icon(painter = painterResource(id = R.drawable.skip_next_24px), contentDescription = "Previous")
            }
            IconButton(onClick = { playerModel.toggleShuffle() }) {
                if (isShuffling){
                    Icon(painter = painterResource(id = R.drawable.shuffle_on_24px), contentDescription = "Shuffle On")
                } else {
                    Icon(painter = painterResource(id = R.drawable.shuffle_24px), contentDescription = "Shuffle Off")
                }
            }
        }
    }
}

@Composable
fun Sources(playerModel: PlayerViewModel) {
    val isPlaying by playerModel.isPlaying.observeAsState(false)
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
            LazyVerticalGrid(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f), columns = GridCells.Adaptive(minSize = 128.dp)) {
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
            Button(onClick = { if(text != ""){playerModel.addMediaItem(text)}
                             if (!isPlaying){playerModel.play()}}, modifier = Modifier.fillMaxWidth()) {
                Icon(painter = painterResource(id = R.drawable.add_circle_24px), contentDescription = "Add")
                Text(text = "Add")
            }
        }
    }
}

@Composable
fun Settings() {
    Column (modifier = Modifier.fillMaxSize()) {

    }
}

@Composable
fun Queue(playerModel: PlayerViewModel) {
    val itemCount by playerModel.mediaItemCount.observeAsState(0)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 16.dp)) {
        if (itemCount == 0){
            Text(text = "Queue is empty!")
        } else {
            LazyColumn (modifier = Modifier.fillMaxWidth()) {
                items(itemCount){ index ->
                    TrackCard(title = playerModel.getMediaItemAt(index)?.mediaMetadata?.artist.toString(), artist = playerModel.getMediaItemAt(index)?.mediaMetadata?.artist.toString(), imageUri = playerModel.getMediaItemAt(index)?.mediaMetadata?.artworkUri)
                }
            }
            Button(onClick = { playerModel.clearMediaItems() }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Clear all")
            }
        }
    }
}