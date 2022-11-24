package ru.cleverpumpkin.calendar.sample.selection.modes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import ru.cleverpumpkin.calendar.sample.BaseComposeFragment
import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarDate
import ru.cleverpumpkin.crunchycalendar.calendarcompose.CalendarScreen
import ru.cleverpumpkin.crunchycalendar.calendarcompose.SelectionMode

class ComposeModeDemoFragment: BaseComposeFragment() {

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ComposeView {
        return super.onCreateView(inflater, container, savedInstanceState).apply {
            setContent {
                Column(modifier = Modifier.fillMaxSize()) {
                    val selected = remember { mutableStateOf(SelectionMode.NONE) }
                    val selectedDates = remember { mutableStateOf(emptyList<CalendarDate>()) }

                    SimpleRadioButtonComponent(
                        selected = selected.value,
                        onClick = {
                            selected.value = it
                        }
                    )

                    CalendarScreen(
                        selectedDates = selectedDates.value,
                        selectionMode = selected.value
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleRadioButtonComponent(
    selected: SelectionMode,
    onClick: ((SelectionMode) -> Unit)
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        SelectionMode.values().forEach { selectionMode ->
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (selectionMode == selected),
                    onClick = {
                        onClick(selectionMode)
                    }
                )

                Text(
                    text = selectionMode.name,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}