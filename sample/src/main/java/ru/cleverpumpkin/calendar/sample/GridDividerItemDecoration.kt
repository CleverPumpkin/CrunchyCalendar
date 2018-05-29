package ru.cleverpumpkin.calendar.sample

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView

/**
 * ItemDecoration implementation that applies and inset margin
 * around each child of the RecyclerView. It also draws item dividers
 * that are expected from a vertical list implementation, such as
 * ListView.
 */
class GridDividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    companion object {

        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }

    private val mDivider: Drawable

    init {
        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        a.recycle()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        drawVertical(c, parent)
        drawHorizontal(c, parent)
    }

    /** Draw dividers at each expected grid interval  */
    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val adapterPosition = parent.getChildAdapterPosition(child)
            if (parent.adapter.getItemViewType(adapterPosition) == DateAdapter.DISABLE_VIEW_TYPE) {
                continue
            }

            val left = child.left - params.leftMargin
            val right = child.right + params.rightMargin
            val top = child.bottom + params.bottomMargin
            val bottom = top + mDivider.intrinsicHeight
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    /** Draw dividers to the right of each child view  */
    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val adapterPosition = parent.getChildAdapterPosition(child)
            if (parent.adapter.getItemViewType(adapterPosition) == DateAdapter.DISABLE_VIEW_TYPE) {
                continue
            }

            val left = child.right + params.rightMargin
            val right = left + mDivider.intrinsicWidth
            val top = child.top - params.topMargin
            val bottom = child.bottom + params.bottomMargin
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }
}