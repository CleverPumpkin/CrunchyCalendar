package ru.cleverpumpkin.calendar.sample.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_demo_demo.*
import ru.cleverpumpkin.calendar.CalendarView
import ru.cleverpumpkin.calendar.sample.R

/**
 * This demo fragment demonstrate usage of the [CalendarView] as a dialog.
 *
 * Created by Alexander Surinov on 2018-06-13.
 */
class DialogDemoFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_demo_demo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        if (savedInstanceState == null) {
            calendarView.setupCalendar(selectionMode = CalendarView.SelectionMode.SINGLE)
        }
    }

}