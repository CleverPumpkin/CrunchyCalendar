package ru.cleverpumpkin.calendar.utils

import android.content.Context
import android.util.TypedValue

fun Context.spToPix(spValue: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, resources.displayMetrics)
}