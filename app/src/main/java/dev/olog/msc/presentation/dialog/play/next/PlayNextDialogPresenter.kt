package dev.olog.msc.presentation.dialog.play.next

import android.support.v4.media.session.MediaControllerCompat
import androidx.core.os.bundleOf
import dev.olog.core.MediaId
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import dev.olog.intents.MusicServiceCustomAction
import javax.inject.Inject

class PlayNextDialogPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val getSongListByParamUseCase: GetSongListByParamUseCase
) {

    fun execute(mediaController: MediaControllerCompat) {
        val items = if (mediaId.isLeaf) {
            listOf(mediaId.leaf!!)
        } else {
            getSongListByParamUseCase(mediaId).map { it.id }
        }
        val bundle = bundleOf(
            MusicServiceCustomAction.ARGUMENT_IS_PODCAST to mediaId.isAnyPodcast,
            MusicServiceCustomAction.ARGUMENT_MEDIA_ID_LIST to items.toLongArray()
        )

        mediaController.transportControls.sendCustomAction(
            MusicServiceCustomAction.ADD_TO_PLAY_NEXT.name,
            bundle
        )
    }

}