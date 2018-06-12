package ru.cleverpumpkin.calendar.sample

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.FragmentTransaction

import android.support.v7.app.AppCompatActivity
import ru.cleverpumpkin.calendar.CalendarDate
import ru.cleverpumpkin.calendar.sample.DemoModeListFragment.DemoMode
import java.util.*

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
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, DemoModeFragment.newInstance(demoMode))
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
}