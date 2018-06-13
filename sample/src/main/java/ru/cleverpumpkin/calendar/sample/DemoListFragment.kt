package ru.cleverpumpkin.calendar.sample

import android.content.Context
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.CalendarView.SelectionMode

class DemoListFragment : Fragment() {

    interface OnDemoModeClickListener {

        fun onDemoModeClick(demoMode: DemoMode)
    }

    enum class DemoMode(
        @StringRes val descriptionRes: Int,
        val selectionMode: CalendarView.SelectionMode
    ) {
        DISPLAY_ONLY(
            descriptionRes = R.string.demo_mode_no_selection,
            selectionMode = SelectionMode.NON
        ),
        SINGLE_SELECTION(
            descriptionRes = R.string.demo_mode_single_selection,
            selectionMode = SelectionMode.SINGLE
        ),
        MULTIPLE_SELECTION(
            descriptionRes = R.string.demo_mode_multiple_selection,
            selectionMode = SelectionMode.MULTIPLE
        ),
        RANGE_SELECTION(
            descriptionRes = R.string.demo_mode_range_selection,
            selectionMode = SelectionMode.RANGE
        ),
        LIMITED_DATES_SELECTION(
            descriptionRes = R.string.demo_mode_limited_selection,
            selectionMode = SelectionMode.MULTIPLE
        ),
        CUSTOM_EVENTS(
            descriptionRes = R.string.demo_mode_events,
            selectionMode = SelectionMode.NON
        ),
        CUSTOM_STYLE(
            descriptionRes = R.string.demo_mode_custom_style,
            selectionMode = SelectionMode.MULTIPLE
        ),
        DIALOG(
            descriptionRes = R.string.demo_mode_dialog,
            selectionMode = SelectionMode.MULTIPLE
        )
    }

    private var demoModeClickListener: OnDemoModeClickListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        demoModeClickListener = context as? OnDemoModeClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_demo_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        recyclerView.run {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(context)
            adapter = DemoModeAdapter()
        }
    }

    private inner class DemoModeAdapter : RecyclerView.Adapter<DemoModeViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoModeViewHolder {
            val context = parent.context
            val view = LayoutInflater.from(context).inflate(R.layout.item_demo_mode, parent, false)
            val holder = DemoModeViewHolder(view as TextView)

            view.setOnClickListener {
                val adapterPosition = holder.adapterPosition

                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val demoMode = DemoMode.values()[adapterPosition]
                    demoModeClickListener?.onDemoModeClick(demoMode)
                }
            }

            return holder
        }

        override fun onBindViewHolder(holder: DemoModeViewHolder, position: Int) {
            val demoMode = DemoMode.values()[position]
            holder.textView.setText(demoMode.descriptionRes)
        }

        override fun getItemCount() = DemoMode.values().size
    }

    private class DemoModeViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
}