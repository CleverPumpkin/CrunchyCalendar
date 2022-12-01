package ru.cleverpumpkin.calendar.sample.demolist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.elevation.SurfaceColors
import ru.cleverpumpkin.calendar.sample.BaseFragment
import ru.cleverpumpkin.calendar.sample.R
import ru.cleverpumpkin.calendar.sample.databinding.FragmentDemoListBinding

class DemoListFragment : BaseFragment() {

    companion object {
        const val GITHUB_ADDRESS = "https://github.com/CleverPumpkin/CrunchyCalendar"
        const val SITE_ADDRESS = "https://cleverpumpkin.ru/"
    }

    interface OnDemoItemSelectionListener {

        fun onDemoItemSelected(demoItem: DemoItem)
    }

    override val layoutRes: Int
        get() = R.layout.fragment_demo_list

    private val viewBinding: FragmentDemoListBinding by viewBinding(FragmentDemoListBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val colorSurface2 = SurfaceColors.SURFACE_2.getColor(requireContext())

        val demoListAdapter = DemoListAdapter(
            onDemoItemClickListener = { demoItem ->
                (activity as? OnDemoItemSelectionListener)?.onDemoItemSelected(demoItem)
            }
        )

        with(viewBinding) {
            toolbar.setBackgroundColor(colorSurface2)

            infoCard.setupInfoCard(
                onGithubClick = {
                    goToWebSite(GITHUB_ADDRESS)
                },
                onSiteClick = {
                    goToWebSite(SITE_ADDRESS)
                }
            )

            with(recyclerView) {
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                layoutManager = LinearLayoutManager(context)
                adapter = demoListAdapter
            }
        }
    }

    private fun goToWebSite(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

}