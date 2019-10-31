package com.example.vali.pubgithub.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

object AnimationUtils {

    fun showViewAnimated(view: View, alpha: Float, duration: Long, visibility: Int) {
        view.visibility = visibility
        view.apply {
            animate()
                .alpha(alpha)
                .setDuration(duration)
                .setListener(null)
        }
    }

    fun hideViewAnimated(view: View, alpha: Float, duration: Long, visibility: Int) {
        view.apply {
            animate()
                .alpha(alpha)
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.visibility = visibility
                    }
                })
        }
    }

}