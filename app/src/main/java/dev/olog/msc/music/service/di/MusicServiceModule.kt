package dev.olog.msc.music.service.di

import android.app.NotificationManager
import android.app.Service
import android.app.UiModeManager
import android.arch.lifecycle.Lifecycle
import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import dagger.Module
import dagger.Provides
import dev.olog.msc.dagger.PerService
import dev.olog.msc.dagger.ServiceContext
import dev.olog.msc.dagger.ServiceLifecycle
import dev.olog.msc.music.service.MusicService
import dev.olog.msc.music.service.QueueManager
import dev.olog.msc.music.service.interfaces.Player
import dev.olog.msc.music.service.interfaces.PlayerLifecycle
import dev.olog.msc.music.service.interfaces.Queue
import dev.olog.msc.music.service.interfaces.ServiceLifecycleController
import dev.olog.msc.music.service.player.PlayerImpl
import dev.olog.msc.music.service.player.PlayerVolume
import dev.olog.msc.music.service.volume.IPlayerVolume
import dev.olog.msc.utils.k.extension.notificationManager

@Module(includes = arrayOf(MusicServiceModule.Binds::class))
class MusicServiceModule(
        private val service: MusicService
) {

    @Provides
    @ServiceContext
    internal fun provideContext(): Context = service

    @Provides
    internal fun provideService(): Service = service

    @Provides
    @PerService
    internal fun provideServiceLifecycle(): ServiceLifecycleController = service


    @Provides
    @ServiceLifecycle
    internal fun provideLifecycle(): Lifecycle = service.lifecycle

    @Provides
    @PerService
    internal fun provideMediaSession(): MediaSessionCompat {
        return MediaSessionCompat(service, MusicService.TAG,
                ComponentName(service, MediaButtonReceiver::class.java),
                null)
    }

    @Provides
    @PerService
    internal fun provideAudioManager(): AudioManager {
        return service.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    @Provides
    @PerService
    internal fun provideUiModeManager(): UiModeManager {
        return service.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    }

    @Provides
    @PerService
    internal fun provideNotificationManager(): NotificationManager {
        return service.notificationManager
    }

    @Provides
    internal fun provideToken(mediaSession: MediaSessionCompat): MediaSessionCompat.Token {
        return mediaSession.sessionToken
    }

    @Provides
    internal fun provideMediaController(mediaSession: MediaSessionCompat): MediaControllerCompat {
        return mediaSession.controller
    }

    @Module
    abstract class Binds {

        @dagger.Binds
        @PerService
        abstract fun provideQueue(queue: QueueManager): Queue

        @dagger.Binds
        @PerService
        abstract fun providePlayer(player: PlayerImpl): Player

        @dagger.Binds
        @PerService
        abstract fun providePlayerLifecycle(player: Player): PlayerLifecycle

        @dagger.Binds
        @PerService
        abstract fun providePlayerVolume(volume: PlayerVolume): IPlayerVolume

    }

}