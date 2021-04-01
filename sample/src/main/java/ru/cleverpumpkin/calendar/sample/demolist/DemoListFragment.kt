package ru.cleverpumpkin.calendar.sample.demolist

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.cleverpumpkin.calendar.sample.BaseFragment
import ru.cleverpumpkin.calendar.sample.R
import ru.cleverpumpkin.calendar.sample.databinding.FragmentDemoListBinding

class DemoListFragment : BaseFragment() {

    interface OnDemoItemSelectionListener {

        fun onDemoItemSelected(demoItem: DemoItem)
    }

    override val layoutRes: Int
        get() = R.layout.fragment_demo_list

    private val viewBinding: FragmentDemoListBinding by viewBinding(FragmentDemoListBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val demoListAdapter = DemoListAdapter(
            onDemoItemClickListener = { demoItem ->
                (activity as? OnDemoItemSelectionListener)?.onDemoItemSelected(demoItem)
            }
        )

        with(viewBinding.recyclerView) {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(context)
            adapter = demoListAdapter
        }
    }

}