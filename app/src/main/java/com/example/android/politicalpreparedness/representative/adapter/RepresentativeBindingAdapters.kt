package com.example.android.politicalpreparedness.representative.adapter

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.representative.model.Representative

@BindingAdapter("profileImage")
fun fetchImage(imageView: ImageView, src: String?) {
    if (src != null) {

        val uri = src.toUri().buildUpon().scheme("https").build()
        Glide.with(imageView).load(uri)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    imageView.setImageResource(R.drawable.ic_profile)
                    return true
                }
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

            })
            .placeholder(R.drawable.loading_animation)
            .transform(CircleCrop())
            .into(imageView)
    } else
        imageView.setImageResource(R.drawable.ic_profile)
}

@BindingAdapter("stateValue")
fun Spinner.setNewValue(value: String?) {
    val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
    val position = when (adapter.getItem(0)) {
        is String -> adapter.getPosition(value)
        else -> this.selectedItemPosition
    }
    if (position >= 0) {
        setSelection(position)
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T> {
    return adapter as ArrayAdapter<T>
}

@BindingAdapter("loading")
fun showloading(view: View, it: Boolean) {
    view.visibility = if (it) View.VISIBLE else View.GONE
}