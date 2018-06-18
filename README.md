# Calendar

Calendar widget that allow displaying calendar grid, selecting dates, displaying color indicators for specific dates and handling date selection with custom action.

![alt text](images/calendar.jpg)

# Sample of usage

First of all, you should declare `CalendarView` in your lyout XML file:

```xml
  <ru.cleverpumpkin.calendar.CalendarView 
        android:id="@+id/calendar_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

In the code perform calendar seting up.

```kotlin

val calendarView = view.findViewById(R.id.calendar_view)
val calendar = Calendar.getInstance()

// Initial date
calendar.set(2018, Calendar.JUNE, 1)
val initialDate = CalendarDate(calendar.time)

// Minimum available date
calendar.set(2018, Calendar.MAY, 15)
val minDate = CalendarDate(calendar.time)

// Maximum available date
calendar.set(2018, Calendar.JULY, 15)
val maxDate = CalendarDate(calendar.time)

// Set up calendar
calendarView.setupCalendar(
    initialDate = initialDate,
    minDate = minDate,
    maxDate = maxDate,
    selectionMode = SelectionMode.NON,
    selectedDates = emptyList()
)
                
```

Calendar is able to save and restore its state, so no needs to call `setupCalendar()` every time, when `Activity` or `Fragment`
recreated. 
