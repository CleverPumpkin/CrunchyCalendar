package ru.cleverpumpkin.calendar.extension

import android.content.Context
import android.util.TypedValue
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun Context.spToPix(spValue: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, resources.displayMetrics)
}

fun Context.dpToPix(dpValue: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, resources.displayMetrics)
}

@ColorInt
fun Context.getColorInt(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}

fun Context.loadAnim(@AnimRes animResId: Int): Animation {
    return AnimationUtils.loadAnimation(this, animResId)
}