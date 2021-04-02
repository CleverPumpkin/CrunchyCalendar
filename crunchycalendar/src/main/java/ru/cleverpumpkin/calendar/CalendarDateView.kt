package ru.cleverpumpkin.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import ru.cleverpumpkin.calendar.extension.dpToPix
import ru.cleverpumpkin.calendar.extension.getColorInt
import ru.cleverpumpkin.calendar.extension.spToPix

/**
 * This internal view class represents a single date cell of the Calendar with optional
 * colored indicators.
 *
 * This view class control its drawable state with [isToday], [cellSelectionState], [isDateDisabled]
 * and [isWeekend] properties.
 */
internal class CalendarDateView @JvmOverloads constructor(
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
        private val stateDateSelectedSingle = intArrayOf(R.attr.calendar_state_selected_single)
        private val stateDateSelectedStart = intArrayOf(R.attr.calendar_state_selected_start)
        private val stateDateSelectedEnd = intArrayOf(R.attr.calendar_state_selected_end)
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

    private var dayNumberCalculatedWidth = 0.0f
    private var currentStateTextColor: Int = getColorInt(R.color.calendar_date_text_color)

    var textColorStateList: ColorStateList? = null

    var isToday: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                refreshDrawableState()
            }
        }

    var cellSelectionState: DateCellSelectedState = DateCellSelectedState.NOT_SELECTED
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
            isLongClickable = value.not()
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
            dayNumberCalculatedWidth = textPaint.measureText(value)
        }

    var dateIndicators: List<CalendarView.DateIndicator> = emptyList()
        set(indicators) {
            field = indicators.take(MAX_INDICATORS_COUNT)
        }

    override fun onDraw(canvas: Canvas) {
        canvas.drawDayNumber()
        canvas.drawIndicators()
    }

    private fun Canvas.drawDayNumber() {
        textPaint.color = currentStateTextColor

        val xPos = width / 2.0f
        val yPos = height / 2.0f - (textPaint.descent() + textPaint.ascent()) / 2.0f

        drawText(dayNumber, xPos - (dayNumberCalculatedWidth / 2.0f), yPos, textPaint)
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

        when(cellSelectionState){
            DateCellSelectedState.NOT_SELECTED -> {

            }
            DateCellSelectedState.SELECTED -> {
                mergeDrawableStates(drawableState, stateDateSelected)
            }
            DateCellSelectedState.SINGLE -> {
                mergeDrawableStates(drawableState, stateDateSelectedSingle)
            }
            DateCellSelectedState.SELECTION_START -> {
                mergeDrawableStates(drawableState, stateDateSelectedStart)
            }
            DateCellSelectedState.SELECTION_END -> {
                mergeDrawableStates(drawableState, stateDateSelectedEnd)
            }
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
        if (stateList != null) {
            currentStateTextColor = stateList.getColorForState(drawableState, currentStateTextColor)
        }
    }

}