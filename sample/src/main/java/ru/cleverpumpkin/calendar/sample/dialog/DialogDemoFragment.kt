package ru.cleverpumpkin.calendar.sample.dialog

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.elevation.SurfaceColors
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.sample.databinding.FragmentDemoDemoBinding

/**
 * This demo fragment demonstrate usage of the [CalendarView] as a dialog.
 *
 * Created by Alexander Surinov on 2018-06-13.
 */
class DialogDemoFragment : BottomSheetDialogFragment() {

    private val viewBinding: FragmentDemoDemoBinding by viewBinding(FragmentDemoDemoBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentDemoDemoBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val colorSurface2 = SurfaceColors.SURFACE_2.getColor(requireContext())
        viewBinding.dialog.backgroundTintList = ColorStateList.valueOf(colorSurface2)

        if (savedInstanceState == null) {
            with(viewBinding.calendarView) {
                setDaysBarBackgroundColor(colorSurface2)
                setYearSelectionBarBackgroundColor(colorSurface2)

                setupCalendar(selectionMode = CalendarView.SelectionMode.SINGLE)
            }
        }
    }

}