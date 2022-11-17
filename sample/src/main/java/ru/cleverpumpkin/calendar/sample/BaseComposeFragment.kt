package ru.cleverpumpkin.calendar.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment

abstract class BaseComposeFragment : Fragment {

    constructor() : super()

    constructor(@LayoutRes layoutRes: Int) : super(layoutRes)

    /**
     * Override with *false* for fragment where BottomBar needs to hidden
     *
     * *Hosting activity must implement [BottomBarVisibilityHandler]*
     */
    open val withBottomBar: Boolean = true

    /**
     * Override with *false* if status bar is always dark
     */
    open val withLightStatusBar: Boolean = true

    /**
     * Override with *false* if nav bar is always dark
     */
    open val withLightNavBar: Boolean = true

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ComposeView {
        return ComposeView(requireContext())
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
    }

}