package ru.cleverpumpkin.calendar.sample.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.sample.R
import ru.cleverpumpkin.calendar.sample.databinding.FragmentDemoDemoBinding

/**
 * This demo fragment demonstrate usage of the [CalendarView] as a dialog.
 *
 * Created by Alexander Surinov on 2018-06-13.
 */
class DialogDemoFragment : DialogFragment() {

    private val viewBinding: FragmentDemoDemoBinding by viewBinding(FragmentDemoDemoBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_demo_demo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            viewBinding.calendarView.setupCalendar(selectionMode = CalendarView.SelectionMode.SINGLE)
        }
    }

}