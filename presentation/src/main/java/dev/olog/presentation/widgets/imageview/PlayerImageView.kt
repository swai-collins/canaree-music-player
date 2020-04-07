package dev.olog.presentation.widgets.imageview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.bumptech.glide.Priority
import dev.olog.domain.MediaId
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.image.provider.GlideUtils
import dev.olog.presentation.ripple.RippleTarget
import dev.olog.presentation.widgets.imageview.shape.ShapeImageView
import dev.olog.shared.widgets.adaptive.AdaptiveColorImageViewPresenter

open class PlayerImageView (
    context: Context,
    attr: AttributeSet

) : ShapeImageView(context, attr) {

    private val presenter = AdaptiveColorImageViewPresenter(this)

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        if (!isInEditMode) {
            presenter.onNextImage(bm)
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (!isInEditMode) {
            presenter.onNextImage(drawable)
        }
    }

    fun observeProcessorColors() = presenter.observeProcessorColors()
    fun observePaletteColors() = presenter.observePaletteColors()

    open fun loadImage(mediaId: MediaId) {
        GlideApp.with(context).clear(this)

        GlideApp.with(context)
            .load(mediaId)
            .error(CoverUtils.getGradient(context, mediaId))
            .priority(Priority.IMMEDIATE)
            .override(GlideUtils.OVERRIDE_SMALL)
            .onlyRetrieveFromCache(true)
            .into(RippleTarget(this@PlayerImageView))
    }

}

