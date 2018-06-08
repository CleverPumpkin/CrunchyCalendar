package ru.cleverpumpkin.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.support.annotation.AttrRes
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import ru.cleverpumpkin.calendar.utils.getColorInt
import ru.cleverpumpkin.calendar.utils.spToPix

class CalendarDateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0

) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_TEXT_SIZE = 12.0f

        private val stateToday = intArrayOf(R.attr.cpcalendar_state_today)
        private val stateDateSelected = intArrayOf(R.attr.cpcalendar_state_selected)
        private val stateDateDisabled = intArrayOf(R.attr.cpcalendar_state_disabled)
    }

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = context.spToPix(DEFAULT_TEXT_SIZE)
    }

    private var textWidth = 0.0f
    private var textColor: Int = context.getColorInt(R.color.calendar_date_text_color)

    var isToday: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                refreshDrawableState()
            }
        }

    var isDateSelected: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                refreshDrawableState()
            }
        }

    var isDateDisabled: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                refreshDrawableState()
            }
            isClickable = value.not()
        }

    var text: String = ""
        set(value) {
            field = value
            textWidth = textPaint.measureText(value)
        }

    var textColorStateList: ColorStateList? = null

    override fun onDraw(canvas: Canvas) {
        textPaint.color = textColor

        val xPos = (canvas.width / 2).toFloat()
        val yPos = (canvas.height / 2 - (textPaint.descent() + textPaint.ascent()) / 2)

        canvas.drawText(text, xPos - (textWidth / 2), yPos, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredWidth)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 3)

        if (isToday) {
            mergeDrawableStates(drawableState, stateToday)
        }

        if (isDateSelected) {
            mergeDrawableStates(drawableState, stateDateSelected)
        }

        if (isDateDisabled) {
            mergeDrawableStates(drawableState, stateDateDisabled)
        }

        return drawableState
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()

        val stateList = textColorStateList
        if (stateList != null && stateList.isStateful) {
            textColor = stateList.getColorForState(drawableState, textColor)
        }
    }
}