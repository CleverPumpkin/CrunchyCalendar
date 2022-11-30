package ru.cleverpumpkin.calendar.sample

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.elevation.SurfaceColors
import ru.cleverpumpkin.calendar.sample.customstyle.CodeStylingDemoFragment
import ru.cleverpumpkin.calendar.sample.customstyle.CodeStylingSecondDemoFragment
import ru.cleverpumpkin.calendar.sample.dateboundaries.DateBoundariesDemoFragment
import ru.cleverpumpkin.calendar.sample.demolist.DemoItem
import ru.cleverpumpkin.calendar.sample.demolist.DemoListFragment
import ru.cleverpumpkin.calendar.sample.dialog.DialogDemoFragment
import ru.cleverpumpkin.calendar.sample.events.EventListDemoFragment
import ru.cleverpumpkin.calendar.sample.selection.SelectionModesDemoFragment

class MainActivity : AppCompatActivity(), DemoListFragment.OnDemoItemSelectionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val colorSurface2 = SurfaceColors.SURFACE_2.getColor(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = colorSurface2

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, DemoListFragment())
                .commit()
        }
    }

    override fun onDemoItemSelected(demoItem: DemoItem) {
        val demoFragment = when (demoItem) {
            DemoItem.SELECTION -> SelectionModesDemoFragment()
            DemoItem.DATE_BOUNDARIES -> DateBoundariesDemoFragment()
            DemoItem.STYLING -> CodeStylingDemoFragment()
            DemoItem.STYLING_SECOND -> CodeStylingSecondDemoFragment()
            DemoItem.EVENTS -> EventListDemoFragment()
            DemoItem.DIALOG -> DialogDemoFragment()
        }

        if (demoFragment is DialogDemoFragment) {
            demoFragment.show(supportFragmentManager, null)
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, demoFragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        }
    }

}