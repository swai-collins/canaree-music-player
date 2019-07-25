package dev.olog.presentation.recentlyadded

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import dev.olog.core.interactor.GetItemTitleUseCase
import dev.olog.core.interactor.ObserveRecentlyAddedUseCase
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.shared.android.extensions.asLiveData
import dev.olog.shared.android.extensions.mapToList
import kotlinx.coroutines.rx2.asFlowable
import javax.inject.Inject

class RecentlyAddedFragmentViewModel @Inject constructor(
    mediaId: MediaId,
    useCase: ObserveRecentlyAddedUseCase,
    getItemTitleUseCase: GetItemTitleUseCase

) : ViewModel() {

    val itemOrdinal = mediaId.category.ordinal

    val data: LiveData<List<DisplayableItem>> = useCase(mediaId)
        .asFlowable().toObservable()
        .mapToList { it.toRecentDetailDisplayableItem(mediaId) }
        .asLiveData()

    val itemTitle = getItemTitleUseCase.execute(mediaId).asLiveData()

}

private fun Song.toRecentDetailDisplayableItem(parentId: MediaId): DisplayableItem {
    return DisplayableTrack(
        type = R.layout.item_recently_added,
        mediaId = MediaId.playableItem(parentId, id),
        title = title,
        artist = artist,
        album = album,
        idInPlaylist = idInPlaylist
    )
}


