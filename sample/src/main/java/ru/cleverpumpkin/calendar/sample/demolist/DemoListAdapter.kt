package ru.cleverpumpkin.calendar.sample.demolist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.cleverpumpkin.calendar.sample.R

/**
 * Created by Alexander Surinov on 2019-05-13.
 */
class DemoListAdapter(
    private val onDemoItemClickListener: (DemoItem) -> Unit
) : RecyclerView.Adapter<DemoItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoItemViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_demo, parent, false)
        val holder = DemoItemViewHolder(view as TextView)

        view.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val sampleItem = DemoItem.values()[adapterPosition]
                onDemoItemClickListener.invoke(sampleItem)
            }
        }

        return holder
    }

    override fun getItemCount(): Int {
        return DemoItem.values().size
    }

    override fun onBindViewHolder(holder: DemoItemViewHolder, position: Int) {
        val demoItem = DemoItem.values()[position]
        holder.titleView.setText(demoItem.titleRes)
    }

}