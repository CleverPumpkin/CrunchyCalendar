package ru.cleverpumpkin.calendar.sample

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import ru.cleverpumpkin.calendar.sample.DemoModeListFragment.DemoMode
import ru.cleverpumpkin.calendar.sample.DemoModeListFragment.DemoMode.*

class MainActivity : AppCompatActivity(), DemoModeListFragment.OnDemoModeClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, DemoModeListFragment())
                .commit()
        }
    }

    override fun onDemoModeClick(demoMode: DemoMode) {
        val fragment = when (demoMode) {
            DISPLAY_ONLY, SINGLE_SELECTION, MULTIPLE_SELECTION, RANGE_SELECTION -> {
                CalendarDateSelectionFragment.newInstance(demoMode)
            }
            LIMITED_DATES_SELECTION -> {
                CalendarLimitedDateSelectionFragment()
            }
            CUSTOM_EVENTS -> {
                CalendarWithEventsFragment.newInstance(demoMode)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
}