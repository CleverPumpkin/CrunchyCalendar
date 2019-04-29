package ru.cleverpumpkin.calendar.extension

import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

@ColorInt
fun View.getColorInt(@ColorRes colorRes: Int): Int {
    return this.context.getColorInt(colorRes)
}