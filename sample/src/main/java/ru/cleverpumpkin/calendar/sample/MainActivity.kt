package ru.cleverpumpkin.calendar.sample

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import ru.cleverpumpkin.calendar.sample.DemoListFragment.DemoMode
import ru.cleverpumpkin.calendar.sample.DemoListFragment.DemoMode.*

class MainActivity : AppCompatActivity(), DemoListFragment.OnDemoModeClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, DemoListFragment())
                .commit()
        }
    }

    override fun onDemoModeClick(demoMode: DemoMode) {
        val fragment = when (demoMode) {
            DISPLAY_ONLY, SINGLE_SELECTION, MULTIPLE_SELECTION, RANGE_SELECTION -> {
                CalendarWithDateSelectionFragment.newInstance(demoMode)
            }
            LIMITED_DATES_SELECTION -> {
                CalendarWithLimitedDateSelectionFragment()
            }
            CUSTOM_EVENTS -> {
                CalendarWithEventsFragment()
            }
            CUSTOM_STYLE -> {
                CalendarWithCustomStyleFragment()
            }
            DIALOG -> {
                CalendarDialogFragment()
            }
        }

        if (demoMode == DIALOG) {
            val dialogFragment = fragment as DialogFragment
            dialogFragment.show(supportFragmentManager, null)
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        }
    }
}