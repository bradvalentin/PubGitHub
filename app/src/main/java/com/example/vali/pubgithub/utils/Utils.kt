package com.example.vali.pubgithub.utils

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.vali.pubgithub.R

const val ALPHA_0 = 0f
const val ALPHA_1 = 1f
const val ANIMATION_DURATION = 200L

inline fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

@BindingAdapter("android:onSingleClick")
fun View.setOnSingleClickListener(onSafeClick: () -> Unit) {
    val singleClickListener = SingleClickListener {
        onSafeClick()
    }
    setOnClickListener(singleClickListener)
}

fun ImageView.loadImageFromUrl(url: String?) {
    val options = RequestOptions()
        .circleCrop()
        .placeholder(R.drawable.baseline_account_box_white_48)
        .error(R.drawable.baseline_account_box_white_48)

    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(url)
        .into(this)
}

@BindingAdapter("android:imageUrl")
fun loadImage(view: ImageView, url: String?) {
    view.loadImageFromUrl(url)
}

@BindingAdapter("android:animatedVisibility")
fun setAnimatedVisibility(target: View, isLoading: Boolean) {
    if (isLoading) AnimationUtils.showViewAnimated(target, ALPHA_1, ANIMATION_DURATION, View.VISIBLE)
    else AnimationUtils.hideViewAnimated(target, ALPHA_0, ANIMATION_DURATION, View.GONE)
}

@BindingAdapter("android:animatedVisibility")
fun setAnimatedVisibility(target: RecyclerView, isLoading: Boolean) {
    if (!isLoading) AnimationUtils.showViewAnimated(target, ALPHA_1, ANIMATION_DURATION, View.VISIBLE)
    else AnimationUtils.hideViewAnimated(target, ALPHA_0, ANIMATION_DURATION, View.GONE)
}