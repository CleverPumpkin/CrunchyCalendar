package ru.cleverpumpkin.calendar.sample

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

class DateAdapter(
    private val items: List<DisplayableItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var formatter = SimpleDateFormat("LLLL yyyy", Locale.getDefault())

    companion object {
        const val DATE_VIEW_TYPE = 0
        const val DISABLE_VIEW_TYPE = 1
        const val MONTH_VIEW_TYPE = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DateItem -> DATE_VIEW_TYPE
            is DisableItem -> DISABLE_VIEW_TYPE
            is MonthItem -> MONTH_VIEW_TYPE
            else -> throw IllegalStateException("Unknown view type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DATE_VIEW_TYPE -> {
                val v =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
                DateViewHolder(v)
            }

            DISABLE_VIEW_TYPE -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_disable, parent, false)
                DisableViewHolder(v)
            }

            MONTH_VIEW_TYPE -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_month, parent, false)
                MonthViewHolder(v)
            }
            else -> throw IllegalStateException("Unknown view type")
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            DATE_VIEW_TYPE -> {
                val item = items[position] as DateItem
                val dateViewHolder = holder as DateViewHolder

                val dayOfMonth = item.dayOfMonth
                dateViewHolder.textView.text = dayOfMonth.toString()
            }

            MONTH_VIEW_TYPE -> {
                val item = items[position] as MonthItem
                val dateViewHolder = holder as MonthViewHolder

                val title = formatter.format(item.date)
                dateViewHolder.textView.text = title
            }
        }
    }

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView as TextView
    }

    class DisableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class MonthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView as TextView
    }
}