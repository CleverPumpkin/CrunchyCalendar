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
        private const val DEFAULT_ADDITIONAL_TEXT_SIZE = 11.0f
        private const val INDICATOR_RADIUS = 3f
        private const val SPACE_BETWEEN_INDICATORS = 2.0f
        private const val MAX_INDICATORS_COUNT = 4
        private const val MAX_ADDITIONAL_TEXT_COUNT = 1
        private const val INDICATORS_AREA_CORNER_RADIUS = 16f
        //value to place additional text between dayNumber an dateCell's bottom
        private const val ADDITIONAL_TEXT_Y_POS_MODIFIER = 1.3f

        private val stateToday = intArrayOf(R.attr.calendar_state_today)
        private val stateDateSelected = intArrayOf(R.attr.calendar_state_selected)
        private val stateDateSelectedFirstInLine = intArrayOf(R.attr.calendar_state_selected_first_in_line)
        private val stateDateSelectedLastInLine = intArrayOf(R.attr.calendar_state_selected_last_in_line)
        private val stateDateSelectedSingle = intArrayOf(R.attr.calendar_state_selected_single)
        private val stateDateSelectedStart = intArrayOf(R.attr.calendar_state_selected_start)
        private val stateDateSelectedEnd = intArrayOf(R.attr.calendar_state_selected_end)
        private val stateDateSelectedStartWithoutMiddle = intArrayOf(R.attr.calendar_state_selected_start_without_middle)
        private val stateDateSelectedEndWithoutMiddle = intArrayOf(R.attr.calendar_state_selected_end_without_middle)
        private val stateDateDisabled = intArrayOf(R.attr.calendar_state_disabled)
        private val stateWeekend = intArrayOf(R.attr.calendar_state_weekend)
    }

    private val radiusPx = context.dpToPix(INDICATOR_RADIUS)
    private val spacePx = context.dpToPix(SPACE_BETWEEN_INDICATORS)
    private val indicatorsAreaCornerRadius = context.dpToPix(INDICATORS_AREA_CORNER_RADIUS)

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = context.spToPix(DEFAULT_TEXT_SIZE)
    }

    private val indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private var dayNumberCalculatedWidth = 0.0f
    private var currentStateTextColor: Int = getColorInt(R.color.calendar_date_text_color)
    var indicatorAreaColor: Int = getColorInt(R.color.calendar_date_background)

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

    var additionalTexts: List<CalendarView.AdditionalText> = emptyList()
        set(texts) {
            field = texts.take(MAX_ADDITIONAL_TEXT_COUNT)
        }

    override fun onDraw(canvas: Canvas) {
        canvas.drawDayNumber()
        canvas.drawIndicators()
        canvas.drawAdditionalTexts()
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
        val yPos = height - height / 4.0f

        drawIndicatorsBackground(drawableAreaWidth, yPos)

        dateIndicators.forEach { indicator ->
            indicatorPaint.color = indicator.color
            drawCircle(xPos, yPos, radiusPx, indicatorPaint)

            xPos += radiusPx * 2.0f + spacePx
        }
    }

    private fun Canvas.drawAdditionalTexts() {
        if (additionalTexts.isEmpty()) {
            return
        }

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = context.spToPix(DEFAULT_ADDITIONAL_TEXT_SIZE)
        }

        val xPos = width / 2.0f
        val yPos = height / ADDITIONAL_TEXT_Y_POS_MODIFIER - (textPaint.descent() + textPaint.ascent()) / ADDITIONAL_TEXT_Y_POS_MODIFIER

        additionalTexts.forEach { additionalText ->
            textPaint.color = additionalText.color
            drawText(additionalText.text, xPos - (textPaint.measureText(additionalText.text) / 2.0f), yPos, textPaint)
        }

    }

    private fun Canvas.drawIndicatorsBackground(
        drawableAreaWidth: Float,
        yPos: Float
    ) {
        val drawedIndicatorsArea = drawableAreaWidth + spacePx + spacePx
        val left = (width - drawedIndicatorsArea) / 2
        val right = drawedIndicatorsArea + left
        val top = yPos - radiusPx - spacePx
        val bottom = yPos + radiusPx + spacePx

        indicatorPaint.color = indicatorAreaColor
        drawRoundRect(
            left,
            top,
            right,
            bottom,
            indicatorsAreaCornerRadius,
            indicatorsAreaCornerRadius,
            indicatorPaint
        )
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

        when (cellSelectionState) {
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
            DateCellSelectedState.SELECTED_FIRST_IN_LINE -> {
                mergeDrawableStates(drawableState, stateDateSelectedFirstInLine)
            }
            DateCellSelectedState.SELECTED_LAST_IN_LINE -> {
                mergeDrawableStates(drawableState, stateDateSelectedLastInLine)
            }
            DateCellSelectedState.SELECTION_START_WITHOUT_MIDDLE -> {
                mergeDrawableStates(drawableState, stateDateSelectedStartWithoutMiddle)
            }
            DateCellSelectedState.SELECTION_END_WITHOUT_MIDDLE -> {
                mergeDrawableStates(drawableState, stateDateSelectedEndWithoutMiddle)
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