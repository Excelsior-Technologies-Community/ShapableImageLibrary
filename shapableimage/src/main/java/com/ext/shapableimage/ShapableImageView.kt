package com.ext.shapableimagelibrary

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.ext.shapableimage.R

class ShapableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var shapeType = 0
    private var borderColor = Color.WHITE
    private var borderWidth = 0f
    private var cornerRadius = 0f

    private val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        context.obtainStyledAttributes(attrs, R.styleable.ShapableImageView).apply {
            shapeType = getInt(R.styleable.ShapableImageView_shapeType, 0)
            borderColor = getColor(R.styleable.ShapableImageView_borderColor, Color.WHITE)
            borderWidth = getDimension(R.styleable.ShapableImageView_borderWidth, 0f)
            cornerRadius = getDimension(R.styleable.ShapableImageView_cornerRadius, 0f)
            recycle()
        }

        borderPaint.style = Paint.Style.STROKE
        borderPaint.color = borderColor
        borderPaint.strokeWidth = borderWidth
    }

    override fun onDraw(canvas: Canvas) {
        val drawable = drawable ?: return
        val bitmap = drawableToBitmap(drawable) ?: return

        val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val matrix = Matrix()

        val scaleX = width.toFloat() / bitmap.width.toFloat()
        val scaleY = height.toFloat() / bitmap.height.toFloat()
        val scale = maxOf(scaleX, scaleY)

        matrix.setScale(scale, scale)
        shader.setLocalMatrix(matrix)

        imagePaint.shader = shader

        val w = width.toFloat()
        val h = height.toFloat()

        when (shapeType) {
            0 -> { // Circle
                val radius = minOf(w, h) / 2f
                canvas.drawCircle(w / 2f, h / 2f, radius, imagePaint)

                if (borderWidth > 0)
                    canvas.drawCircle(w / 2f, h / 2f, radius - borderWidth / 2f, borderPaint)
            }

            1 -> { // Rounded
                val rect = RectF(0f, 0f, w, h)
                canvas.drawRoundRect(rect, cornerRadius, cornerRadius, imagePaint)

                if (borderWidth > 0)
                    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint)
            }

            2 -> { // Oval
                val rect = RectF(0f, 0f, w, h)
                canvas.drawOval(rect, imagePaint)

                if (borderWidth > 0)
                    canvas.drawOval(rect, borderPaint)
            }
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) return null

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}
