package ru.cleverpumpkin.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.support.v4.widget.ImageViewCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class YearSelectionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0

) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val YEAR_FORMAT = "yyyy"
    }

    private val arrowPrevView: ImageView
    private val arrowNextView: ImageView
    private val yearTextView: TextView

    private val yearFormatter = SimpleDateFormat(YEAR_FORMAT, Locale.getDefault())
    private var minMaxDatesRange = NullableDatesRange()

    var displayedDate: CalendarDate = CalendarDate.today
        set(newDate) {
            val currentDisplayedDate = field
            field = newDate

            if (currentDisplayedDate.year != newDate.year) {
                yearTextView.text = yearFormatter.format(newDate.date)
                updateArrowButtonsState()
            }
        }

    var onYearChangeListener: ((CalendarDate) -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_year_selection, this, true)

        arrowPrevView = findViewById(R.id.arrow_prev)
        arrowNextView = findViewById(R.id.arrow_next)
        yearTextView = findViewById(R.id.year_text_view)

        yearTextView.text = yearFormatter.format(displayedDate.date)

        val arrowClickListener = OnClickListener { v ->
            val (minDate, maxDate) = minMaxDatesRange

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

            onYearChangeListener?.invoke(displayedDate)
        }

        arrowPrevView.setOnClickListener(arrowClickListener)
        arrowNextView.setOnClickListener(arrowClickListener)
    }

    fun setupYearSelectionView(displayedDate: CalendarDate, minMaxDatesRange: NullableDatesRange) {
        this.minMaxDatesRange = minMaxDatesRange
        this.displayedDate = displayedDate
        updateArrowButtonsState()
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

    fun applyStyle(style: YearSelectionStyle) {
        setBackgroundColor(style.background)
        ImageViewCompat.setImageTintList(arrowPrevView, ColorStateList.valueOf(style.arrowsColor))
        ImageViewCompat.setImageTintList(arrowNextView, ColorStateList.valueOf(style.arrowsColor))
        yearTextView.setTextColor(style.yearTextColor)
    }

    class YearSelectionStyle(
        @ColorInt val background: Int,
        @ColorInt val arrowsColor: Int,
        @ColorInt val yearTextColor: Int
    )
}