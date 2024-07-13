package com.brickerfixer.turnable.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController

class PlayerViewModel : ViewModel() {

    private val _mediaItemCount = MutableLiveData<Int>()
    val mediaItemCount: LiveData<Int> = _mediaItemCount

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _currentTrack = MutableLiveData<String?>()
    val currentTrack: LiveData<String?> = _currentTrack

    private val _currentArtist = MutableLiveData<String?>()
    val currentArtist: LiveData<String?> = _currentArtist

    private val _shuffleModeEnabled = MutableLiveData<Boolean>()
    val shuffleModeEnabled: LiveData<Boolean> = _shuffleModeEnabled

    private val _repeatMode = MutableLiveData<Int>()
    val repeatMode: LiveData<Int> = _repeatMode

    private var mediaController: MediaController? = null

    fun initializeMediaController(context: Context, onInitialized: () -> Unit) {
        MediaControllerManager.initialize(context) { controller ->
            mediaController = controller
            mediaController?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.postValue(isPlaying)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    _currentTrack.postValue(mediaMetadata.title.toString())
                    _currentArtist.postValue(mediaMetadata.artist.toString())
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
            })
            onInitialized()
        }
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
    }

    fun clearMediaItems() {
        mediaController?.clearMediaItems()
    }

    fun getMediaItemAt(index: Int): MediaItem? {
        return mediaController?.getMediaItemAt(index)
    }

    fun seekTo(positionMs: Long){
        mediaController?.seekTo(positionMs)
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

    override fun onCleared() {
        super.onCleared()
        MediaControllerManager.release()
    }
}