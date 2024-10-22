package com.brickerfixer.turnable.viewmodel

import android.content.Context
import android.media.session.PlaybackState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.brickerfixer.turnable.model.Track
import com.brickerfixer.turnable.model.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PlayerViewModel @Inject constructor(val repository: TrackRepository) : ViewModel() {

    private val _mediaItemCount = MutableLiveData<Int>()
    val mediaItemCount: LiveData<Int> = _mediaItemCount

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _playbackState = MutableLiveData<Int>()
    val playbackState: LiveData<Int> = _playbackState

    private val _currentTrack = MutableLiveData<String?>()
    val currentTrack: LiveData<String?> = _currentTrack

    private val _currentArtist = MutableLiveData<String?>()
    val currentArtist: LiveData<String?> = _currentArtist

    private val _shuffleModeEnabled = MutableLiveData<Boolean>()
    val shuffleModeEnabled: LiveData<Boolean> = _shuffleModeEnabled

    private val _repeatMode = MutableLiveData<Int>()
    val repeatMode: LiveData<Int> = _repeatMode

    private var mediaController: MediaController? = null

    private val _currentPosition = MutableLiveData<Long>()
    val currentPosition: LiveData<Long> = _currentPosition

    private val _trackDuration = MutableLiveData<Long>()
    val trackDuration: LiveData<Long> = _trackDuration

    lateinit var tracks : List<Track>

    fun initializeMediaController(context: Context, onInitialized: () -> Unit) {
        MediaControllerManager.initialize(context) { controller ->
            mediaController = controller
            mediaController?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.postValue(isPlaying)
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    _currentTrack.postValue(mediaMetadata.title.toString())
                    _currentArtist.postValue(mediaMetadata.artist.toString())
                    if (mediaMetadata.title != null){
                        _trackDuration.postValue(mediaController?.duration)
                    }
                }

                override fun onRepeatModeChanged(repeatMode: Int) {
                    _repeatMode.postValue(repeatMode)
                }

                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                    _shuffleModeEnabled.postValue(shuffleModeEnabled)
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    _mediaItemCount.postValue(mediaController?.mediaItemCount ?: 0)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    _playbackState.postValue(playbackState)
                }

                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                    _currentPosition.postValue(mediaController?.currentPosition)
                }
            })
            onInitialized()
        }
    }

    fun play() {
        mediaController?.play()
    }

    fun pause() {
        mediaController?.pause()
    }

    fun togglePlayback() {
        mediaController?.let {
            if (it.isPlaying) {
                it.pause()
            } else {
                it.play()
            }
        }
    }

    fun seekToPrevious() {
        mediaController?.seekToPreviousMediaItem()
    }

    fun seekToNext() {
        mediaController?.seekToNextMediaItem()
    }

    fun toggleShuffle() {
        mediaController?.shuffleModeEnabled = mediaController?.shuffleModeEnabled?.not() ?: false
    }

    fun addMediaItem(uri: String) {
        val mediaItem = MediaItem.fromUri(uri)
        mediaController?.addMediaItem(mediaItem)
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNewItemToDB(uri)
        }
    }

    fun clearMediaItems() {
        mediaController?.clearMediaItems()
    }

    fun getMediaItemAt(index: Int): MediaItem? {
        return mediaController?.getMediaItemAt(index)
    }
    fun updateAll() {
        viewModelScope.launch(Dispatchers.IO) {
            tracks = repository.getAll()
        }
    }

    fun removeItem(index: Int) {
        mediaController?.removeMediaItem(index)
    }

    fun toggleRepeat(){
        when(mediaController?.repeatMode){
            Player.REPEAT_MODE_OFF -> {
                mediaController?.repeatMode = Player.REPEAT_MODE_ALL
            }

            Player.REPEAT_MODE_ALL -> {
                mediaController?.repeatMode = Player.REPEAT_MODE_ONE
            }

            Player.REPEAT_MODE_ONE -> {
                mediaController?.repeatMode = Player.REPEAT_MODE_OFF
            }
        }
    }

    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
        _currentPosition.postValue(position)
    }

    override fun onCleared() {
        super.onCleared()
        MediaControllerManager.release()
    }
}