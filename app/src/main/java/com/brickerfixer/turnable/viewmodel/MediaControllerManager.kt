package com.brickerfixer.turnable.viewmodel

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.brickerfixer.turnable.model.ExoplayerService
import com.google.common.util.concurrent.MoreExecutors

object MediaControllerManager {
    private var mediaController: MediaController? = null

    @OptIn(UnstableApi::class)
    fun initialize(context: Context, onInitialized: (MediaController) -> Unit) {
        val sessionToken = SessionToken(context, ComponentName(context, ExoplayerService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                mediaController = controllerFuture.get()
                onInitialized(mediaController!!)
            },
            MoreExecutors.directExecutor()
        )
    }

    fun getMediaController(): MediaController? {
        return mediaController
    }

    fun release() {
        mediaController?.release()
        mediaController = null
    }
}