package ru.cleverpumpkin.calendar.sample.events

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ru.cleverpumpkin.calendar.sample.R

/**
 * Created by Alexander Surinov on 2019-05-13.
 */
class EventDialogAdapter(
    context: Context,
    events: Array<EventItem>
) : ArrayAdapter<EventItem>(context, 0, events) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dialog_event, parent, false)

        val eventItem = getItem(position)

        if (eventItem != null) {
            view.findViewById<View>(R.id.colorView).setBackgroundColor(eventItem.color)
            view.findViewById<TextView>(R.id.eventNameView).text = eventItem.eventName
        }

        return view
    }

}