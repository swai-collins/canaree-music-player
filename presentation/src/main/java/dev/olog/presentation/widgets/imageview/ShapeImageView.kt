package dev.olog.presentation.widgets.imageview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import dev.olog.presentation.R
import dev.olog.shared.android.extensions.dipf
import dev.olog.shared.android.extensions.lazyFast
import dev.olog.shared.android.theme.HasImageShape
import dev.olog.shared.android.theme.ImageShape
import dev.olog.shared.android.widgets.ForegroundImageView
import kotlinx.coroutines.*

open class ShapeImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null

) : ForegroundImageView(context, attrs) {

    companion object {
        private const val DEFAULT_RADIUS = 5
        @JvmStatic
        private val X_FERMO_MODE = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    private val hasImageShape by lazyFast { context.applicationContext as HasImageShape }

    private var job: Job? = null

    private val radius: Int
    private var mask: Bitmap? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.RoundedCornersImageView)
        radius = a.getInt(
            R.styleable.RoundedCornersImageView_imageViewCornerRadius,
            DEFAULT_RADIUS
        )
        a.recycle()

        clipToOutline = true

        paint.xfermode = X_FERMO_MODE
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) {
            return
        }
        val hasImageShape = context.applicationContext as HasImageShape
        job = GlobalScope.launch(Dispatchers.Default) {
            for (imageShape in hasImageShape.observeImageShape()) {
                mask = null
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job?.cancel()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isInEditMode) {
            getMask()?.let { canvas.drawBitmap(it, 0f, 0f, paint) }
        }
    }

    private fun getMask(): Bitmap? {
        try {
            if (mask == null) {
                mask = when (hasImageShape.getImageShape()) {
                    ImageShape.ROUND -> {
                        setLayerType(View.LAYER_TYPE_HARDWARE, null)
                        val drawable =
                            ContextCompat.getDrawable(context, R.drawable.shape_rounded_corner)!! as GradientDrawable
                        drawable.cornerRadius = context.dipf(radius)
                        drawable.toBitmap(width, height, Bitmap.Config.ALPHA_8)
                    }
                    ImageShape.RECTANGLE -> {
                        setLayerType(View.LAYER_TYPE_NONE, null)
                        null
                    }
                }
            }
            return mask
        } catch (ex: Exception){
            // TODO big image is throwing exception when changing song
            ex.printStackTrace()
            return null
        }
    }

}