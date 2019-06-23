package dev.olog.image.provider.fetcher

import android.content.Context
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.core.MediaId
import dev.olog.core.gateway.FolderGateway2
import dev.olog.core.gateway.GenreGateway2
import dev.olog.core.gateway.PlaylistGateway2
import dev.olog.image.provider.creator.ImagesFolderUtils
import dev.olog.image.provider.creator.MergedImagesCreator
import dev.olog.image.provider.executor.GlideScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.io.File
import java.io.InputStream

class GlideMergedImageFetcher(
    private val context: Context,
    private val mediaId: MediaId,
    private val folderGateway: FolderGateway2,
    private val playlistGateway: PlaylistGateway2,
    private val genreGateway: GenreGateway2
) : DataFetcher<InputStream>, CoroutineScope by GlideScope() {

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        launch {
            withTimeout(2000) {
                val inputStream = when {
                    mediaId.isFolder -> makeFolderImage(mediaId.categoryValue)
                    mediaId.isGenre -> makeGenreImage(mediaId.categoryId)
                    else -> makePlaylistImage(mediaId.categoryId)
                }
                callback.onDataReady(inputStream)
            }
        }
    }


    private suspend fun makeFolderImage(folder: String): InputStream? {
//        val folderImage = ImagesFolderUtils.forFolder(context, dirPath) --contains current image
        val albumsId = folderGateway.getTrackListByParam(folder).map { it.albumId }

        val folderName = ImagesFolderUtils.FOLDER
        val normalizedPath = folder.replace(File.separator, "")

        val file = MergedImagesCreator.makeImages(
            context = context,
            albumIdList = albumsId,
            parentFolder = folderName,
            itemId = normalizedPath
        )
        return file?.inputStream()
    }

    private suspend fun makeGenreImage(genreId: Long): InputStream? {
//        ImagesFolderUtils.forGenre(context, id) --contains current image

        val albumsId = genreGateway.getTrackListByParam(genreId).map { it.albumId }

        val folderName = ImagesFolderUtils.GENRE
        val file = MergedImagesCreator.makeImages(
            context = context,
            albumIdList = albumsId,
            parentFolder = folderName,
            itemId = "$genreId"
        )
        return file?.inputStream()
    }

    private suspend fun makePlaylistImage(playlistId: Long): InputStream? {
//        if (PlaylistGateway.isAutoPlaylist(playlistId) || PodcastPlaylistGateway.isPodcastAutoPlaylist(playlistId)){ TODO
//            return null
//        }

//        ImagesFolderUtils.forPlaylist(context, id) --contains current image
        val albumsId = playlistGateway.getTrackListByParam(playlistId).map { it.albumId }

        val folderName = ImagesFolderUtils.PLAYLIST
        val file = MergedImagesCreator.makeImages(
            context = context,
            albumIdList = albumsId,
            parentFolder = folderName,
            itemId = "$playlistId"
        )
        return file?.inputStream()
    }

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    override fun getDataSource(): DataSource = DataSource.LOCAL

    override fun cleanup() {
        cancel(null)
    }

    override fun cancel() {
        cancel(null)
    }

}