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

    var displayedYear: CalendarDate = CalendarDate.today
        set(value) {
            if (field.year != value.year) {
                yearTextView.text = yearFormatter.format(value.date)
            }

            field = value
        }

    var onYearChangeListener: ((CalendarDate) -> Boolean)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_year_selection, this, true)

        arrowPrevView = findViewById(R.id.arrow_prev)
        arrowNextView = findViewById(R.id.arrow_next)
        yearTextView = findViewById(R.id.year_text_view)

        yearTextView.text = yearFormatter.format(displayedYear.date)

        val arrowClickListener = OnClickListener { v ->
            val newDisplayedYear = if (v.id == R.id.arrow_prev) {
                displayedYear.prevYear()
            } else {
                displayedYear.nextYear()
            }

            val updateDisplayedYear = onYearChangeListener?.invoke(newDisplayedYear) ?: true
            if (updateDisplayedYear) {
                displayedYear = newDisplayedYear
            }
        }

        arrowPrevView.setOnClickListener(arrowClickListener)
        arrowNextView.setOnClickListener(arrowClickListener)
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