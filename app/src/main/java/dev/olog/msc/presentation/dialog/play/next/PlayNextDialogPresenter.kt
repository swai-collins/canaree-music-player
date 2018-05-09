package dev.olog.msc.presentation.dialog.play.next

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaControllerCompat
import dev.olog.msc.domain.interactor.all.GetSongListByParamUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PlayNextDialogPresenter @Inject constructor(
        private val mediaId: MediaId,
        private val getSongListByParamUseCase: GetSongListByParamUseCase
) {

    fun execute(mediaController: MediaControllerCompat): Completable {
        return if (mediaId.isLeaf){
            Single.fromCallable { "${mediaId.leaf!!}" }.subscribeOn(Schedulers.io())
        } else {
            getSongListByParamUseCase.execute(mediaId)
                    .firstOrError()
                    .map { it.map { it.id }.joinToString() }
        }.map { mediaController.addQueueItem(newMediaDescriptionItem(it), Int.MAX_VALUE) }
                .toCompletable()
    }

    private fun newMediaDescriptionItem(songId: String): MediaDescriptionCompat {
        return MediaDescriptionCompat.Builder()
                .setMediaId(songId)
                .build()
    }

}