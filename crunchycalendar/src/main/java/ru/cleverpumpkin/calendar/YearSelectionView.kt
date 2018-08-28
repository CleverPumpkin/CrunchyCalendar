package ru.cleverpumpkin.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.support.v4.widget.ImageViewCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class YearSelectionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0

) : LinearLayout(context, attrs, defStyleAttr) {

    private val arrowLeftView: ImageView
    private val arrowRightView: ImageView
    private val yearTextView: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_year_selection, this, true)

        arrowLeftView = findViewById(R.id.arrow_left)
        arrowRightView = findViewById(R.id.arrow_right)
        yearTextView = findViewById(R.id.year_text_view)
    }

    fun applyStyle(style: YearSelectionStyle) {
        setBackgroundColor(style.background)
        ImageViewCompat.setImageTintList(arrowLeftView, ColorStateList.valueOf(style.arrowsColor))
        ImageViewCompat.setImageTintList(arrowRightView, ColorStateList.valueOf(style.arrowsColor))
        yearTextView.setTextColor(style.yearTextColor)
    }

    class YearSelectionStyle(
        @ColorInt val background: Int,
        @ColorInt val arrowsColor: Int,
        @ColorInt val yearTextColor: Int
    )
}