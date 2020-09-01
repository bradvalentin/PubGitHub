package com.example.vali.pubgithub.utils

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.circleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.vali.pubgithub.R

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