package ru.cleverpumpkin.calendar.decorations

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import ru.cleverpumpkin.calendar.R
import ru.cleverpumpkin.calendar.adapter.CalendarAdapter
import ru.cleverpumpkin.calendar.utils.dpToPix
import ru.cleverpumpkin.calendar.utils.getColorInt

class GridDividerItemDecoration(
    context: Context,
    @ColorInt dividerColor: Int = context.getColorInt(R.color.calendar_grid_color)
) : RecyclerView.ItemDecoration() {

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = dividerColor
        strokeWidth = context.dpToPix(1.0f)
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val adapterPosition = parent.getChildAdapterPosition(child)

            if (parent.adapter.getItemViewType(adapterPosition) == CalendarAdapter.DATE_VIEW_TYPE) {

                canvas.drawRect(
                    child.left.toFloat(),
                    child.top.toFloat(),
                    child.right.toFloat(),
                    child.bottom.toFloat(),
                    linePaint
                )
            }
        }
    }
}