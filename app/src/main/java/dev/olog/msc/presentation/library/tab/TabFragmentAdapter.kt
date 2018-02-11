package dev.olog.msc.presentation.library.tab

import android.arch.lifecycle.Lifecycle
import android.databinding.ViewDataBinding
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import dagger.Lazy
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.constants.Constants
import dev.olog.msc.dagger.FragmentLifecycle
import dev.olog.msc.presentation.MusicController
import dev.olog.msc.presentation.base.adapter.BaseListAdapter
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.widget.fast.scroller.FastScrollerSectionIndexer
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.elevateAlbumOnTouch
import dev.olog.msc.utils.k.extension.elevateSongOnTouch
import dev.olog.msc.utils.k.extension.setOnClickListener
import dev.olog.msc.utils.k.extension.setOnLongClickListener
import javax.inject.Inject

class TabFragmentAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle,
        private val navigator: Navigator,
        private val musicController: MusicController,
        private val viewModel: TabFragmentViewModel,
        private val lastPlayedArtistsAdapter: Lazy<TabFragmentLastPlayedArtistsAdapter>,
        private val lastPlayedAlbumsAdapter: Lazy<TabFragmentLastPlayedAlbumsAdapter>

) : BaseListAdapter<DisplayableItem>(lifecycle), FastScrollerSectionIndexer {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int) {
        when (viewType) {
            R.layout.item_tab_shuffle -> {
                viewHolder.setOnClickListener(dataController) { _, _ ->
                    musicController.playShuffle(MediaId.shuffleAllId())
                }
            }
            R.layout.item_tab_album,
            R.layout.item_tab_song -> {
                viewHolder.setOnClickListener(dataController) { item, _ ->
                    if (item.isPlayable){
                        musicController.playFromMediaId(item.mediaId)
                    } else {
                        navigator.toDetailFragment(item.mediaId)
                        when (item.mediaId.category){
                            MediaIdCategory.ARTIST -> {
                                viewModel.insertArtistLastPlayed(item.mediaId)
                                        .subscribe({}, Throwable::printStackTrace)
                            }
                            MediaIdCategory.ALBUM -> {
                                viewModel.insertAlbumLastPlayed(item.mediaId)
                                        .subscribe({}, Throwable::printStackTrace)
                            }
                        }
                    }
                }
                viewHolder.setOnLongClickListener(dataController) { item, position ->
                    navigator.toDialog(item, viewHolder.itemView)
                }
                viewHolder.setOnClickListener(R.id.more, dataController) { item, _, view ->
                    navigator.toDialog(item, view)
                }
            }
            R.layout.item_tab_last_played_album_horizontal_list -> {
                val view = viewHolder.itemView as RecyclerView
                setupHorizontalList(view, lastPlayedAlbumsAdapter.get())
            }
            R.layout.item_tab_last_played_artist_horizontal_list -> {
                val view = viewHolder.itemView as RecyclerView
                setupHorizontalList(view, lastPlayedArtistsAdapter.get())
            }
        }

        when (viewType){
            R.layout.item_tab_album -> viewHolder.elevateAlbumOnTouch()
            R.layout.item_tab_song -> viewHolder.elevateSongOnTouch()
        }
    }

    private fun setupHorizontalList(list: RecyclerView, adapter: BaseListAdapter<*>){
        val layoutManager = LinearLayoutManager(list.context, LinearLayoutManager.HORIZONTAL, false)
        list.layoutManager = layoutManager
        list.adapter = adapter
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.quickAction, Constants.quickAction)
        binding.setVariable(BR.musicController, musicController)
    }

    override fun getSectionText(position: Int): String? {
        val item = dataController[position]
        val itemType = item.type
        if (itemType == R.layout.item_tab_song || itemType == R.layout.item_tab_album) {
            return item.title[0].toString().capitalize()
        } else {
            return null
        }
    }

}