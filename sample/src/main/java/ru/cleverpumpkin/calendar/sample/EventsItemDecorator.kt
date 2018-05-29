package ru.cleverpumpkin.calendar.sample

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.util.Log

class EventsItemDecorator() : RecyclerView.ItemDecoration() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.RED
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        Log.d("Draw", "EventsItemDecorator.onDrawOver")

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val adapterPosition = parent.getChildAdapterPosition(child)
            if (parent.adapter.getItemViewType(adapterPosition) == DateAdapter.DATE_VIEW_TYPE ) {

                val left = child.left - params.leftMargin
                val top = child.bottom + params.bottomMargin

                canvas.drawCircle(left.toFloat() + 50, top.toFloat() - 50, 16f, paint)
            }
        }
    }
}