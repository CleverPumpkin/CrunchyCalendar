package ru.cleverpumpkin.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.support.annotation.AttrRes
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import ru.cleverpumpkin.calendar.utils.dpToPix
import ru.cleverpumpkin.calendar.utils.getColorInt
import ru.cleverpumpkin.calendar.utils.spToPix

/**
 * This view class represents a single date cell of calendar with optional colored indicators.
 *
 * This view class control its drawable state with [isToday], [isDateSelected], [isDateDisabled]
 * and [isWeekend] properties.
 */
class CalendarDateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0

) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_TEXT_SIZE = 14.0f
        private const val INDICATOR_RADIUS = 3.5f
        private const val SPACE_BETWEEN_INDICATORS = 4.0f
        private const val MAX_INDICATORS_COUNT = 4

        private val stateToday = intArrayOf(R.attr.calendar_state_today)
        private val stateDateSelected = intArrayOf(R.attr.calendar_state_selected)
        private val stateDateDisabled = intArrayOf(R.attr.calendar_state_disabled)
        private val stateWeekend = intArrayOf(R.attr.calendar_state_weekend)
    }

    private val radiusPx = context.dpToPix(INDICATOR_RADIUS)
    private val spacePx = context.dpToPix(SPACE_BETWEEN_INDICATORS)

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = context.spToPix(DEFAULT_TEXT_SIZE)
    }

    private val indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private var dayNumberWidth = 0.0f
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

    var isWeekend: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                refreshDrawableState()
            }
        }

    var dayNumber: String = ""
        set(value) {
            field = value
            dayNumberWidth = textPaint.measureText(value)
        }

    var dateIndicators: List<CalendarView.DateIndicator> = emptyList()
        set(indicators) {
            field = indicators.take(MAX_INDICATORS_COUNT)
        }

    var textColorStateList: ColorStateList? = null

    override fun onDraw(canvas: Canvas) {
        canvas.drawDayNumber()
        canvas.drawIndicators()
    }

    private fun Canvas.drawDayNumber() {
        textPaint.color = textColor

        val xPos = width / 2.0f
        val yPos = height / 2.0f - (textPaint.descent() + textPaint.ascent()) / 2.0f

        drawText(dayNumber, xPos - (dayNumberWidth / 2.0f), yPos, textPaint)
    }

    private fun Canvas.drawIndicators() {
        if (dateIndicators.isEmpty()) {
            return
        }

        val indicatorsCount = dateIndicators.size
        val drawableAreaWidth = radiusPx * 2.0f * indicatorsCount + spacePx * (indicatorsCount - 1)

        var xPos = ((width - drawableAreaWidth) / 2.0f) + radiusPx
        val yPos = height - height / 6.0f

        dateIndicators.forEach { indicator ->
            indicatorPaint.color = indicator.color
            drawCircle(xPos, yPos, radiusPx, indicatorPaint)

            xPos += radiusPx * 2.0f + spacePx
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        setMeasuredDimension(size, size)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 4)

        if (isToday) {
            mergeDrawableStates(drawableState, stateToday)
        }

        if (isDateSelected) {
            mergeDrawableStates(drawableState, stateDateSelected)
        }

        if (isDateDisabled) {
            mergeDrawableStates(drawableState, stateDateDisabled)
        }

        if (isWeekend) {
            mergeDrawableStates(drawableState, stateWeekend)
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