package com.arctouch.codechallenge.ui.listeners

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class RecyclerItemClickListener(
        context: Context,
        recyclerView: RecyclerView,
        listener: OnItemClickListener
) : RecyclerView.OnItemTouchListener {

    private var mListener: OnItemClickListener? = null
    private var mGestureDetector: GestureDetector? = null

    init {
        mListener = listener
        mGestureDetector =
                GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                    override fun onSingleTapUp(e: MotionEvent): Boolean {
                        return true
                    }

                    override fun onLongPress(e: MotionEvent) {
                        val childView = recyclerView.findChildViewUnder(e.x, e.y)
                        if (childView != null && mListener != null) {
                            mListener?.onItemLongClick(
                                    childView,
                                    recyclerView.getChildLayoutPosition(childView)
                            )
                        }
                    }
                })
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val childView = rv.findChildViewUnder(e.x, e.y)

        childView?.let { view ->
            mGestureDetector?.let {

                if (it.onTouchEvent(e)) {
                    mListener?.onItemClick(view, rv.getChildLayoutPosition(view))
                }
            }

        }

        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }

}