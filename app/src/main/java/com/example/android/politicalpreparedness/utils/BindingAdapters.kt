package com.example.android.politicalpreparedness.utils

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.R
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("textDate")
fun textDate(textView: TextView, date: Date?) {
    if (date == null)
        textView.text = ""
    else {
        val format = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US)
        textView.text = format.format(date)
    }
}

@BindingAdapter("followText")
fun followText(button: AppCompatButton, isFollow: Boolean) {
    if (isFollow)
        button.setText(R.string.unfollow_election)
    else
        button.setText(R.string.follow_election)
}

@BindingAdapter("goneIfNotNull")
fun goneIfNotNull(view: View, it: Any?) {
    view.visibility = if (it != null) View.GONE else View.VISIBLE
}

@BindingAdapter("goneIfNull")
fun goneIfNull(view: View, it: Any?) {
    view.visibility = if (it == null) View.GONE else View.VISIBLE
}
