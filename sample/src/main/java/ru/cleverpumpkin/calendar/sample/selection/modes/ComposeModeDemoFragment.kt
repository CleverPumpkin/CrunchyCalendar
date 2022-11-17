package ru.cleverpumpkin.calendar.sample.selection.modes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.compose.ui.platform.ComposeView
import ru.cleverpumpkin.calendar.sample.BaseComposeFragment
import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarScreen

class ComposeModeDemoFragment: BaseComposeFragment() {

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ComposeView {
        return super.onCreateView(inflater, container, savedInstanceState).apply {
            setContent {
                CalendarScreen()
            }
        }
    }
}