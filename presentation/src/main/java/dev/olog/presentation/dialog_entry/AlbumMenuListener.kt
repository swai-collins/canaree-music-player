package dev.olog.presentation.dialog_entry

import android.content.Context
import android.view.MenuItem
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.GetDetailTabsVisibilityUseCase
import dev.olog.domain.interactor.detail.SetDetailTabsVisiblityUseCase
import dev.olog.domain.interactor.detail.item.GetAlbumUseCase
import dev.olog.presentation.R
import dev.olog.presentation.dagger.ActivityContext
import dev.olog.presentation.navigation.Navigator
import dev.olog.shared.MediaIdHelper
import javax.inject.Inject

class AlbumMenuListener @Inject constructor(
        @ActivityContext context: Context,
        getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator,
        private val getAlbumUseCase: GetAlbumUseCase,
        getDetailTabVisibilityUseCase: GetDetailTabsVisibilityUseCase,
        setDetailTabVisibilityUseCase: SetDetailTabsVisiblityUseCase

) : BaseMenuListener(context, getSongListByParamUseCase, navigator,
        getDetailTabVisibilityUseCase, setDetailTabVisibilityUseCase) {

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        when (itemId){
            R.id.viewArtist -> {
                getAlbumUseCase.execute(item.mediaId)
                        .map { MediaIdHelper.artistId(it.artistId) }
                        .firstOrError()
                        .doOnSuccess { navigator.toDetailFragment(it) }
                        .toCompletable()
                        .subscribe()
                return true
            }
        }
        return super.onMenuItemClick(menuItem)
    }

}
