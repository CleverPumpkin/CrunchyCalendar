package ru.cleverpumpkin.calendar.utils

import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.view.View

@ColorInt
fun View.getColorInt(@ColorRes colorRes: Int): Int {
    return this.context.getColorInt(colorRes)
}