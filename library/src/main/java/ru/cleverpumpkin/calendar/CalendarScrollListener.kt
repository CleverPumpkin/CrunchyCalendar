package ru.cleverpumpkin.calendar

import android.os.Handler
import android.os.Looper
import android.support.v7.widget.RecyclerView

class CalendarScrollListener(
    private val onTheTop: Runnable,
    private val onTheBottom: Runnable

) : RecyclerView.OnScrollListener() {

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val lastChildIndex = recyclerView.layoutManager.childCount
        val lastChild = recyclerView.layoutManager.getChildAt(lastChildIndex - 1)
        val lastChildAdapterPosition = recyclerView.getChildAdapterPosition(lastChild) + 1

        if (recyclerView.adapter.itemCount == lastChildAdapterPosition) {
            mainThreadHandler.post(onTheBottom)
        }

        val firstChild = recyclerView.layoutManager.getChildAt(0)
        val firstChildAdapterPosition = recyclerView.getChildAdapterPosition(firstChild)

        if (firstChildAdapterPosition == 0) {
            mainThreadHandler.post(onTheTop)
        }
    }
}