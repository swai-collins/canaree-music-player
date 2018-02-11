package dev.olog.msc.presentation.splash

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import dev.olog.msc.R
import dev.olog.msc.presentation.GlideApp
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.presentation.widget.SwipeableImageView
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.img.CoverUtils
import kotlinx.android.synthetic.main.fragment_splash_tutorial.view.*

class SplashFragmentTutorial : BaseFragment() {

    private var progressive = 0

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        loadImage(view.cover, progressive)
        view.title.text = getString(R.string.splash_hiding_text) + " "+ TextUtils.HIDING_FACE // hiding monkey
    }

    override fun onResume() {
        super.onResume()

        view!!.cover.setOnSwipeListener(object : SwipeableImageView.SwipeListener {

            override fun onSwipedLeft() {
                loadNextImage()
            }

            override fun onSwipedRight() {
                loadPreviousImage()
            }

            override fun onClick() {
                val newState = !view!!.coverLayout.isActivated
                view!!.coverLayout.isActivated = newState
                view!!.nowPlaying.isActivated = newState
            }
        })

        view!!.fakeNext.setOnClickListener { loadNextImage() }
        view!!.fakePrevious.setOnClickListener { loadPreviousImage() }
    }

    override fun onPause() {
        super.onPause()
        view!!.coverLayout.setOnClickListener(null)
        view!!.cover.setOnSwipeListener(null)
        view!!.fakeNext.setOnClickListener(null)
        view!!.fakePrevious.setOnClickListener(null)
    }

    private fun loadNextImage(){
        progressive++
        loadImage(view!!.cover, progressive)
    }

    private fun loadPreviousImage(){
        progressive--
        loadImage(view!!.cover, progressive)
    }

    private fun loadImage(view: ImageView, position: Int){
        GlideApp.with(context!!)
                .load(Uri.EMPTY)
                .centerCrop()
                .placeholder(CoverUtils.getGradient(context!!, position = position))
                .into(view)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_splash_tutorial
}