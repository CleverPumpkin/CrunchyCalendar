package ru.cleverpumpkin.calendar.sample.selection

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.elevation.SurfaceColors
import ru.cleverpumpkin.calendar.sample.BaseFragment
import ru.cleverpumpkin.calendar.sample.R
import ru.cleverpumpkin.calendar.sample.SelectionFragmentsAction
import ru.cleverpumpkin.calendar.sample.databinding.FragmentSelectionModesBinding
import ru.cleverpumpkin.calendar.sample.selection.modes.*

/**
 * Created by Alexander Surinov on 2019-05-13.
 */
class SelectionModesDemoFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_selection_modes

    private val viewBinding: FragmentSelectionModesBinding by viewBinding(
        FragmentSelectionModesBinding::bind
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding.toolbar) {
            val colorSurface2 = SurfaceColors.SURFACE_2.getColor(requireContext())
            setBackgroundColor(colorSurface2)
            setTitle(R.string.demo_selection)
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { activity?.onBackPressed() }

            setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.move_to_today) {
                    (childFragmentManager.fragments.first() as SelectionFragmentsAction).moveToToday()
                } else {
                    changeSelectionModeDemoFragment(item.itemId)
                }
                true
            }
        }

        if (savedInstanceState == null) {
            changeSelectionModeDemoFragment(R.id.selection_mode_none)
        }
    }

    private fun changeSelectionModeDemoFragment(selectedModeId: Int) {
        val selectionDemoFragment = when (selectedModeId) {
            R.id.selection_mode_none -> NoneSelectionModeDemoFragment()
            R.id.selection_mode_single -> SingleSelectionModeDemoFragment()
            R.id.selection_mode_multiple -> MultipleSelectionModeDemoFragment()
            R.id.selection_mode_range -> RangeSelectionModeDemoFragment()
            R.id.selection_mode_week -> WeekSelectionModeDemoFragment()
            else -> throw IllegalAccessException("Unknown selected mode id")
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.childFragmentContainer, selectionDemoFragment)
            .commit()
    }

}