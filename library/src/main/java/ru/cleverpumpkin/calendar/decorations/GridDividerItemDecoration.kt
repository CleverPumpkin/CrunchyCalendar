package ru.cleverpumpkin.calendar.decorations

import android.graphics.Canvas
import android.graphics.Color

import android.graphics.Paint
import android.graphics.RectF
import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter

class GridDividerItemDecoration(
    @ColorInt dividerColor: Int = Color.GRAY
) : RecyclerView.ItemDecoration() {

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.GRAY
    }

    private val lineRect = RectF()

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)

            val adapterPosition = parent.getChildAdapterPosition(child)
            if (parent.adapter.getItemViewType(adapterPosition) != CalendarAdapter.DAY_VIEW_TYPE) {
                continue
            }

            lineRect.set(
                child.left.toFloat(),
                child.top.toFloat(),
                child.right.toFloat(),
                child.bottom.toFloat()
            )

            c.drawRect(lineRect, linePaint)
        }
    }
}