package ru.cleverpumpkin.calendar.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val items = generateCalendar()

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = DateAdapter(items)

        val layoutManager = GridLayoutManager(this, 7)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {

            override fun getSpanSize(position: Int): Int {
                return when (recyclerView.adapter.getItemViewType(position)) {
                    DateAdapter.MONTH_VIEW_TYPE -> 7
                    else -> 1
                }
            }
        }

        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(GridDividerItemDecoration(this))
        recyclerView.addItemDecoration(EventsItemDecorator())
    }

    private fun generateCalendar(): List<DisplayableItem> {
        val calendarItems = mutableListOf<DisplayableItem>()

        val weekDays = mutableListOf<Int>()

        val firstDayOfWeek = Calendar.getInstance().firstDayOfWeek
        var currentDay = firstDayOfWeek

        repeat(7) {
            if (currentDay > 7) {
                currentDay = 1
            }

            weekDays += currentDay
            currentDay++
        }

        (0..11).forEach { month ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MONTH, month)

            val monthItem = MonthItem(calendar.time)

            val monthItems = generateItemsForMonth(month, weekDays)
            calendarItems.add(monthItem)
            calendarItems += monthItems
        }

        return calendarItems
    }

    private fun generateItemsForMonth(
        month: Int,
        weekDays: List<Int>
    ): List<DisplayableItem> {

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)

        val indexOfFirstDayOfMonth = weekDays.indexOf(firstDayOfMonth)

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val lastDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)
        val indexOfLastDayOfMonth = weekDays.indexOf(lastDayOfMonth)

        val startOffset = indexOfFirstDayOfMonth
        val endOffset = 6 - indexOfLastDayOfMonth

        calendar.set(Calendar.DAY_OF_MONTH, 1)

        // populate data
        val items = mutableListOf<DisplayableItem>()

        (1..startOffset).forEach { items += DisableItem() }

        (1..daysInMonth).forEach {
            val date = calendar.time
            items += DateItem(date, calendar.get(Calendar.DAY_OF_MONTH))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        (1..endOffset).forEach { items += DisableItem() }

        return items
    }
}
