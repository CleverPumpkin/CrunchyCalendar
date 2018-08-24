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

class SampleListFragment : Fragment() {

    interface OnSampleItemClickListener {

        fun onSampleItemClick(sampleItem: SampleItem)
    }

    enum class SampleItem(@StringRes val descriptionRes: Int) {
        SELECTION_SAMPLE(descriptionRes = R.string.selection_sample),
        CUSTOM_STYLE_SAMPLE(descriptionRes = R.string.custom_style_sample),
        DATE_INDICATORS_SAMPLE(descriptionRes = R.string.date_indicators_sample),
        DIALOG_SAMPLE(descriptionRes = R.string.dialog_sample)
    }

    private var sampleItemClickListener: OnSampleItemClickListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        sampleItemClickListener = context as? OnSampleItemClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sample_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        recyclerView.run {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(context)
            adapter = SampleListAdapter()
        }
    }

    private inner class SampleListAdapter : RecyclerView.Adapter<SampleItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleItemViewHolder {
            val context = parent.context
            val view = LayoutInflater.from(context).inflate(R.layout.item_sample, parent, false)
            val holder = SampleItemViewHolder(view as TextView)

            view.setOnClickListener {
                val adapterPosition = holder.adapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val sampleItem = SampleItem.values()[adapterPosition]
                    sampleItemClickListener?.onSampleItemClick(sampleItem)
                }
            }

            return holder
        }

        override fun onBindViewHolder(holder: SampleItemViewHolder, position: Int) {
            val sampleItem = SampleItem.values()[position]
            holder.textView.setText(sampleItem.descriptionRes)
        }

        override fun getItemCount() = SampleItem.values().size
    }

    private class SampleItemViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
}