package ru.cleverpumpkin.calendar

/**
 * @author Anatolii Shulipov (as@cleverpumpkin.ru)
 */
enum class DateCellSelectedState {

    NOT_SELECTED,
    SELECTED,
    SELECTED_FIRST_IN_LINE,
    SELECTED_LAST_IN_LINE,
    SINGLE,
    SELECTION_START,
    SELECTION_START_WITHOUT_MIDDLE,
    SELECTION_END,
    SELECTION_END_WITHOUT_MIDDLE

}