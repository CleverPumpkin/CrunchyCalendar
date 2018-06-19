# Calendar

Calendar widget that allow displaying vertical scrolled calendar grid, selecting dates and 
displaying color indicators for specific dates.

![alt text](images/calendar.jpg)

## Sample of Usage

Here's a basic example of Calendar usage.
 
First of all, you should declare `CalendarView` in your layout XML file.

```xml
  <ru.cleverpumpkin.calendar.CalendarView 
        android:id="@+id/calendar_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

In your `Activity` or `Fragment` class perform setting up.

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

// Set up calendar with all available parameters
calendarView.setupCalendar(
    initialDate = initialDate, 
    minDate = minDate,
    maxDate = maxDate,
    selectionMode = SelectionMode.NON,
    selectedDates = emptyList()
)
                
```


## Saving and Restoring state  
Calendar is able to save and restore its internal state (selected dates, selection mode, etc.), 
so no needs to save it manually and setting up the calendar with `setupCalendar()` method every time, 
when `Activity` or `Fragment` recreated. 

If Calendar was set up with `setupCalendar()` method **before** restoring state, previous saved 
state will be ignored. 

## Dates Selection
Calendar supports several selection modes for dates selecting: **single**, **multiple** and **range**.

#### Single date selection 
Only one date will be able for selection. If there is already selected date and you select another one, previous
selected date will be unselected.

```kotlin

// Set up calendar with SelectionMode.SINGLE
calendarView.setupCalendar(selectionMode = SelectionMode.SINGLE)

...

// Get selected date or null
val selectedDate: CalendarDate? = calendarView.selectedDate

// Get list with single selected date or empty list
val selectedDates: List<CalendarDate> = calendarView.selectedDates
```

#### Multiple date selection 
Multiple dates will be able for selection. Selecting an already selected date will unselect it.

```kotlin

// Set up calendar with SelectionMode.MULTIPLE
calendarView.setupCalendar(selectionMode = SelectionMode.MULTIPLE)

...

// Get all selected dates in order they were added or empty list
val selectedDates: List<CalendarDate> = calendarView.selectedDates

```

#### Range date selection 
Allows you to select a date range. Previous selected range is cleared when you select another one.

```kotlin

// Set up calendar with SelectionMode.RANGE
calendarView.setupCalendar(selectionMode = SelectionMode.RANGE)

... 

// Get all selected dates in range (includes start and end) or empty list
val selectedDates: List<CalendarDate> = calendarView.selectedDates

```

## Color Indicators
Calendar is able to display simple color indicators (dots) on the date cell.

Color indicator represents as simple interface, which you can implement in your classes.  

```kotlin

interface DateIndicator {
    val date: CalendarDate // indicator date
    val color: Int // indicator color
}

```

Here's an example of setting indicators to display on the Calendar.
 
```kotlin

// Set up calendar
calendarView.setupCalendar()

// Set List of indicators that will be displayed on the calendar.
val indicators: List<DateIndicator> = generateDatesIndicators()
calendarView.datesIndicators = indicators

calendarView.onDateClickListener = { date ->
    // Get all indicators for specific date
    val indicatorsForDate = calendarView.getDateIndicators(date)
    
    // do something ... 
}

````

## View Customization

Calendar appearance can be customized with XML attributes.


Here's an example of applying custom attributes for changing Calendar appearance.

```xml

<ru.cleverpumpkin.calendar.CalendarView
    android:id="@+id/calendar_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:cpcalendar_grid_on_selected_dates="false"
    app:cpcalendar_date_background="@drawable/custom_date_bg_selector"
    app:cpcalendar_date_text_color="@color/custom_date_text_selector"
    app:cpcalendar_day_bar_background="@color/custom_calendar_days_bar_background"
    app:cpcalendar_day_bar_text_color="@color/custom_calendar_days_bar_text_color"
    app:cpcalendar_grid_color="@color/custom_calendar_grid_color"
    app:cpcalendar_month_text_color="@color/custom_calendar_month_text_color" />

```

If you need to do some custom drawing logic for Calendar, you can implement standard 
`RecyclerView.ItemDecoration` and add it for Calendar using `addCustomItemDecoration()` method.

```kotlin
// Set up calendar
calendarView.setupCalendar()

// Some custom decoration logic 
val customItemDecoration = CustomItemDecoration()
calendarView.addCustomItemDecoration(customItemDecoration)

```    

There is an abstract helper class `AbsDateItemDecoration` that you can use for decoration 
of specific date cell views.
