package com.fire.mangareader.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

object ImageExtensions {
    @JvmStatic
    fun loadWithCrossFade(imageView: ImageView, url: String?) {
        if (url.isNullOrEmpty()) return
        Glide.with(imageView.context)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }

    @JvmStatic
    fun loadWithCrossFadeAndPlaceholder(imageView: ImageView, url: String?, placeholderId: Int) {
        if (url.isNullOrEmpty()) {
            imageView.setImageResource(placeholderId)
            return
        }
        Glide.with(imageView.context)
            .load(url)
            .placeholder(placeholderId)
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }
}
