package ru.cleverpumpkin.calendar.sample.additionaltexts

import android.graphics.Color
import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.elevation.SurfaceColors
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.sample.BaseFragment
import ru.cleverpumpkin.calendar.sample.R
import ru.cleverpumpkin.calendar.sample.databinding.FragmentCalendarBinding
import java.util.*

/**
 * This demo fragment demonstrate usage of the [CalendarView] to display custom additional
 * texts on date cell.
 *
 */
class AdditionalTextsDemoFragment : BaseFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_calendar

    private val viewBinding: FragmentCalendarBinding by viewBinding(FragmentCalendarBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding.toolbarView) {
            val colorSurface2 = SurfaceColors.SURFACE_2.getColor(requireContext())
            setBackgroundColor(colorSurface2)
            setTitle(R.string.demo_additional_texts)
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        viewBinding.calendarView.datesAdditionalTexts = generateAdditionalTextsItems()

        val colorSurface2 = SurfaceColors.SURFACE_2.getColor(requireContext())
        if (savedInstanceState == null) {
            with(viewBinding.calendarView) {
                setDaysBarBackgroundColor(colorSurface2)
                setYearSelectionBarBackgroundColor(colorSurface2)
                setupCalendar(selectionMode = CalendarView.SelectionMode.NONE)
            }
        }
    }

    private fun generateAdditionalTextsItems(): List<AdditionalTextsItem> {
        val calendar = Calendar.getInstance()

        val additionalTextsItem = mutableListOf<AdditionalTextsItem>()

        repeat(10) {
            additionalTextsItem += AdditionalTextsItem(
                date = CalendarDate(calendar.time),
                text = "14 000",
                color = Color.BLUE
            )

            calendar.add(Calendar.DAY_OF_MONTH, 5)
        }

        return additionalTextsItem
    }

}