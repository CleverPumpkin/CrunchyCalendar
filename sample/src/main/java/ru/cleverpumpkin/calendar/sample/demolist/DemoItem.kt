package ru.cleverpumpkin.calendar.sample.demolist

import androidx.annotation.StringRes
import ru.cleverpumpkin.calendar.sample.R

/**
 * Created by Alexander Surinov on 2019-05-13.
 */
enum class DemoItem(@StringRes val titleRes: Int) {
    SELECTION(titleRes = R.string.demo_selection),
    DATE_BOUNDARIES(titleRes = R.string.demo_date_boundaries),
    STYLING(titleRes = R.string.demo_styling),
    STYLING_SECOND(titleRes = R.string.demo_styling_second),
    EVENTS(titleRes = R.string.demo_events),
    DIALOG(titleRes = R.string.demo_dialog)
}