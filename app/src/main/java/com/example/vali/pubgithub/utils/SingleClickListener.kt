package com.example.vali.pubgithub.utils

import android.os.SystemClock
import android.view.View

const val DEFAULT_INTERVAL = 1000
const val LAST_TIME_CLICKED = 0L

class SingleClickListener(
    private var defaultInterval: Int = DEFAULT_INTERVAL,
    private val onSafeCLick: () -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = LAST_TIME_CLICKED
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick()
    }
}