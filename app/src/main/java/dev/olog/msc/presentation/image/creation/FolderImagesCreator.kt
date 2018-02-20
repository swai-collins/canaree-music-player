package dev.olog.msc.presentation.image.creation

import android.content.Context
import android.provider.MediaStore
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.tab.GetAllSongsUseCase
import dev.olog.msc.utils.assertBackgroundThread
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.img.MergedImagesCreator
import io.reactivex.Flowable
import java.io.File
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

class FolderImagesCreator @Inject constructor(
        @ApplicationContext private val ctx: Context,
        private val getAllSongsUseCase: GetAllSongsUseCase,
        private val imagesThreadPool: ImagesThreadPool

) {

    fun execute() : Flowable<*> {
        return getAllSongsUseCase.execute()
                .firstOrError()
                .observeOn(imagesThreadPool.scheduler)
                .map { it.groupBy { it.folderPath } }
                .flattenAsFlowable { it.entries }
                .map { entry -> try {
                    makeImage(entry)
                } catch (ex: Exception){ false }
                }
                .buffer(10)
                .filter { it.reduce { acc, curr -> acc || curr } }
                .doOnNext {
                    ctx.contentResolver.notifyChange(MEDIA_STORE_URI, null)
                }
    }


    private fun makeImage(map: Map.Entry<String, List<Song>>) : Boolean {
        assertBackgroundThread()
        val folderName = ImagesFolderUtils.getFolderName(ImagesFolderUtils.FOLDER)
        val normalizedPath = map.key.replace(File.separator, "")
        return MergedImagesCreator.makeImages(ctx, map.value, folderName, normalizedPath)
    }

}