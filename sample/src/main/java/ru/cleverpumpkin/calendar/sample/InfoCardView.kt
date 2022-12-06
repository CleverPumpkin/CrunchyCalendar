package ru.cleverpumpkin.calendar.sample

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.cleverpumpkin.calendar.sample.databinding.ViewInfoCardBinding

class InfoCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val viewBinding: ViewInfoCardBinding by viewBinding(ViewInfoCardBinding::bind)

    init {
        inflate(context, R.layout.view_info_card, this)
    }

    fun setupInfoCard(
        onGithubClick: () -> Unit,
        onSiteClick: () -> Unit
    ) = with(viewBinding) {
        toGithub.setOnClickListener {
            onGithubClick.invoke()
        }

        toSite.setOnClickListener {
            onSiteClick.invoke()
        }
    }

}