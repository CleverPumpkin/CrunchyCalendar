package ru.cleverpumpkin.calendar.sample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.decorations.AbsDateItemDecoration
import ru.cleverpumpkin.calendar.utils.dpToPix

class EventsDateItemDecoration(
    context: Context,
    private val groupedEvents: Map<CalendarDate, List<CalendarEvent>>
) : AbsDateItemDecoration() {

    companion object {
        private const val INDICATOR_RADIUS = 4.0f
        private const val SPACE_BETWEEN_INDICATORS = 8.0f
    }

    private val radiusPx = context.dpToPix(INDICATOR_RADIUS)
    private val space = context.dpToPix(SPACE_BETWEEN_INDICATORS)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    override fun decorateDateView(canvas: Canvas, date: CalendarDate, dateViewRect: Rect) {
        val eventsForDate = groupedEvents[date]
        if (eventsForDate == null || eventsForDate.isEmpty()) {
            return
        }

        val eventsCount = eventsForDate.size
        val drawableAreaWidth = (radiusPx * 2 * eventsCount) + (space * (eventsCount - 1))

        val dateViewWidth = dateViewRect.width()
        var xPosition = dateViewRect.left + ((dateViewWidth - drawableAreaWidth) / 2) + radiusPx
        val yPosition = dateViewRect.bottom - radiusPx * 2

        // Draw logic
        eventsForDate.forEach { event ->
            paint.color = event.color
            canvas.drawCircle(xPosition, yPosition, radiusPx, paint)

            xPosition += radiusPx + space
        }
    }
}