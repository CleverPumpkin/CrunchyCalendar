package ru.cleverpumpkin.calendar.sample

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import ru.cleverpumpkin.calendar.sample.SampleListFragment.SampleItem

class MainActivity : AppCompatActivity(),
    SampleListFragment.OnSampleItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, SampleListFragment())
                .commit()
        }
    }

    override fun onSampleItemClick(sampleItem: SampleListFragment.SampleItem) {
        val sampleFragment = when (sampleItem) {
            SampleItem.SELECTION_SAMPLE -> SelectionSampleFragment()
            SampleItem.CUSTOM_STYLE_SAMPLE -> CustomStyleSampleFragment()
            SampleItem.DATE_INDICATORS_SAMPLE -> DateIndicatorsSampleFragment()
            SampleItem.DIALOG_SAMPLE -> DialogSampleFragment()
        }

        if (sampleFragment is DialogFragment) {
            sampleFragment.show(supportFragmentManager, null)
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, sampleFragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        }
    }
}