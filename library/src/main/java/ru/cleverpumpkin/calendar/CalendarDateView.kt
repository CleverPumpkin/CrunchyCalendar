package ru.cleverpumpkin.calendar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.annotation.AttrRes
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import ru.cleverpumpkin.calendar.utils.spToPix

class CalendarDateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0

) : View(context, attrs, defStyleAttr) {

    private companion object {

        private const val DEFAULT_TEXT_SIZE = 12.0f
    }

    private val textSizePx = context.spToPix(DEFAULT_TEXT_SIZE)

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = textSizePx
    }

    private var textWidth = 0.0f

    var text: String = ""
        set(value) {
            field = value
            textWidth = textPaint.measureText(value)
        }

    override fun onDraw(canvas: Canvas) {
        val xPos = (canvas.width / 2).toFloat()
        val yPos = (canvas.height / 2 - (textPaint.descent() + textPaint.ascent()) / 2)

        canvas.drawText(text, xPos - (textWidth / 2), yPos, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredWidth)
    }
}