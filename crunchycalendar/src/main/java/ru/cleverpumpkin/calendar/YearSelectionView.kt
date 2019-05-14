package ru.cleverpumpkin.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.core.widget.ImageViewCompat
import ru.cleverpumpkin.calendar.extension.loadAnim
import ru.cleverpumpkin.calendar.style.CalendarStyleAttributes
import java.text.SimpleDateFormat
import java.util.*

/**
 * This internal view class represents a year selection control.
 * It is used as a part of the [CalendarView].
 */
internal class YearSelectionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0

) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val YEAR_FORMAT = "yyyy"
    }

    private val arrowPrevView: ImageView
    private val arrowNextView: ImageView
    private val textSwitcher: TextSwitcher

    private val slideInBottomAnim = context.loadAnim(R.anim.calendar_slide_in_bottom)
    private val slideInTopAnim = context.loadAnim(R.anim.calendar_slide_in_top)
    private val slideOutBottomAnim = context.loadAnim(R.anim.calendar_slide_out_bottom)
    private val slideOutTopAnim = context.loadAnim(R.anim.calendar_slide_out_top)

    private val yearFormatter = SimpleDateFormat(YEAR_FORMAT, Locale.getDefault())
    private var minMaxDatesRange = NullableDatesRange()

    var displayedDate: CalendarDate = CalendarDate.today
        set(newDate) {
            val oldDate = field
            field = newDate

            if (oldDate.year != newDate.year) {
                updateAnimations(oldYear = oldDate.year, newYear = newDate.year)
                textSwitcher.setText(yearFormatter.format(newDate.date))
                updateArrowButtonsState()
            }
        }

    var onYearChangeListener: ((CalendarDate) -> Unit)? = null

    var onYearClickListener: ((Int) -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.calendar_year_selection_view, this, true)

        arrowPrevView = findViewById(R.id.arrow_prev)
        arrowNextView = findViewById(R.id.arrow_next)
        textSwitcher = findViewById(R.id.text_switcher)

        textSwitcher.setText(yearFormatter.format(displayedDate.date))
        textSwitcher.setOnClickListener {
            onYearClickListener?.invoke(displayedDate.year)
        }

        val arrowClickListener = OnClickListener { v ->
            val (minDate, maxDate) = minMaxDatesRange
            val prevDisplayedDate = displayedDate

            displayedDate = if (v.id == R.id.arrow_prev) {
                val prevYear = displayedDate.minusMonths(CalendarDate.MONTHS_IN_YEAR)
                if (minDate == null || minDate <= prevYear) {
                    prevYear
                } else {
                    minDate
                }

            } else {
                val nextYear = displayedDate.plusMonths(CalendarDate.MONTHS_IN_YEAR)
                if (maxDate == null || maxDate >= nextYear) {
                    nextYear
                } else {
                    maxDate
                }
            }

            if (prevDisplayedDate.year != displayedDate.year) {
                onYearChangeListener?.invoke(displayedDate)
            }
        }

        arrowPrevView.setOnClickListener(arrowClickListener)
        arrowNextView.setOnClickListener(arrowClickListener)
    }

    fun setupYearSelectionView(displayedDate: CalendarDate, minMaxDatesRange: NullableDatesRange) {
        this.minMaxDatesRange = minMaxDatesRange
        this.displayedDate = displayedDate
        updateArrowButtonsState()
    }

    private fun updateAnimations(oldYear: Int, newYear: Int) {
        if (newYear > oldYear) {
            textSwitcher.outAnimation = slideOutTopAnim
            textSwitcher.inAnimation = slideInBottomAnim
        } else {
            textSwitcher.outAnimation = slideOutBottomAnim
            textSwitcher.inAnimation = slideInTopAnim
        }
    }

    private fun updateArrowButtonsState() {
        val (minDate, maxDate) = minMaxDatesRange

        if (minDate == null || minDate.year <= displayedDate.year.dec()) {
            arrowPrevView.isClickable = true
            arrowPrevView.alpha = 1.0f
        } else {
            arrowPrevView.isClickable = false
            arrowPrevView.alpha = 0.2f
        }

        if (maxDate == null || maxDate.year >= displayedDate.year.inc()) {
            arrowNextView.isClickable = true
            arrowNextView.alpha = 1.0f
        } else {
            arrowNextView.isClickable = false
            arrowNextView.alpha = 0.2f
        }
    }

    fun applyStyle(styleAttributes: CalendarStyleAttributes) {
        setBackgroundColor(styleAttributes.yearSelectionBackground)

        ImageViewCompat.setImageTintList(
            arrowPrevView,
            ColorStateList.valueOf(styleAttributes.yearSelectionArrowsColor)
        )

        ImageViewCompat.setImageTintList(
            arrowNextView,
            ColorStateList.valueOf(styleAttributes.yearSelectionArrowsColor)
        )

        for (i in 0..textSwitcher.childCount) {
            val textView = textSwitcher.getChildAt(i) as? TextView
            textView?.setTextColor(styleAttributes.yearSelectionTextColor)
        }
    }

}