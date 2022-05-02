package com.elchaninov.espmanager.utils

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.AppCompatTextView
import com.elchaninov.espmanager.R
import com.google.android.material.snackbar.Snackbar

fun View.hide() {
    this.visibility = GONE
}

fun View.show(title: String) {
    this.findViewById<AppCompatTextView>(R.id.progress_bar_title).text = title
    this.visibility = VISIBLE
}

fun View.showErrorSnackBar(message: String){
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
        .apply {
            setAction("Закрыть") { this.dismiss() }
            show()
        }
}


