package ru.cleverpumpkin.calendar.sample.selection

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.cleverpumpkin.calendar.sample.BaseFragment
import ru.cleverpumpkin.calendar.sample.R
import ru.cleverpumpkin.calendar.sample.databinding.FragmentSelectionModesBinding
import ru.cleverpumpkin.calendar.sample.selection.modes.*

/**
 * Created by Alexander Surinov on 2019-05-13.
 */
class SelectionModesDemoFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_selection_modes

    private val viewBinding: FragmentSelectionModesBinding by viewBinding(FragmentSelectionModesBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding.toolbarView) {
            setTitle(R.string.demo_selection)
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        if (savedInstanceState == null) {
            viewBinding.selectionModesRadioGroup.check(R.id.selectionModeNone)
            changeSelectionModeDemoFragment(R.id.selectionModeNone)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        viewBinding.selectionModesRadioGroup.setOnCheckedChangeListener { _, selectedModeId ->
            changeSelectionModeDemoFragment(selectedModeId)
        }
    }

    private fun changeSelectionModeDemoFragment(selectedModeId: Int) {
        val selectionDemoFragment = when (selectedModeId) {
            R.id.selectionModeNone -> NoneSelectionModeDemoFragment()
            R.id.selectionModeNoneSingle -> SingleSelectionModeDemoFragment()
            R.id.selectionModeMultiple -> MultipleSelectionModeDemoFragment()
            R.id.selectionModeRange -> RangeSelectionModeDemoFragment()
            R.id.selectionModeWeek -> WeekSelectionModeDemoFragment()
            else -> throw IllegalAccessException("Unknown selected mode id")
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.childFragmentContainer, selectionDemoFragment)
            .commit()
    }

}