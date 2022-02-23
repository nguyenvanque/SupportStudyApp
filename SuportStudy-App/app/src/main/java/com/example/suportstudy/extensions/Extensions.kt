package com.example.suportstudy.extensions

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

fun View.visible() {
    if (visibility != View.VISIBLE)
        visibility = View.VISIBLE
}

fun View.gone() {
    if (visibility != View.GONE)
        visibility = View.GONE
}

fun View.invisible() {
    if (visibility != View.INVISIBLE)
        visibility = View.INVISIBLE
}

fun View.disable() {
    isEnabled = false
}

fun View.enable() {
    isEnabled = true
}

fun TextView.visible(error: String) {
    visibility = View.VISIBLE
    text = error
}

fun EditText.requestError() {
    requestFocus()
}

fun EditText.normalStatus() {
    background = null
}

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

fun View.isInvisible(): Boolean {
    return visibility == View.GONE || visibility == View.INVISIBLE
}

fun EditText.limitLength(maxLength: Int) {
    filters = arrayOf(InputFilter.LengthFilter(maxLength))
}


@SuppressLint("ClickableViewAccessibility")
fun View.onClick(unit: () -> Unit) {
    val gestureListener = GestureDetector(context, GestureListener())
    if (this is Button) {
        setOnTouchListener { view1: View?, e: MotionEvent ->
            if (gestureListener.onTouchEvent(e)) {
                unit.invoke()
            }
            return@setOnTouchListener false
        }
    } else {
        setOnTouchListener { view1: View?, e: MotionEvent ->
            if (gestureListener.onTouchEvent(e)) {
                unit.invoke()
            }
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    alpha = 0.5f
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    alpha = 1.0f
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_CANCEL -> {
                    alpha = 1.0f
                    return@setOnTouchListener true
                }
            }
            false
        }
    }
}

fun <T> View.debounce(action: (T) -> Unit): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        if (debounceJob == null) {
            debounceJob = CoroutineScope(Dispatchers.Default).launch {
                action(param)
                delay(500)
                debounceJob = null
            }
        }
    }
}

fun View.debounce(action: () -> Unit) {
    var debounceJob: Job? = null
    if (debounceJob == null) {
        debounceJob = CoroutineScope(Dispatchers.Default).launch {
            action.invoke()
            delay(500)
            debounceJob = null
        }
    }
}

private class GestureListener : GestureDetector.SimpleOnGestureListener() {
    override fun onSingleTapUp(event: MotionEvent): Boolean {
        return true
    }
}

fun RecyclerView.getCurrentPosition(): Int {
    return (this.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
}

fun Fragment.push(id: Int) {
    findNavController().navigate(id)
}

fun Fragment.push(id: Int, bundle: Bundle) {
    findNavController().navigate(id, bundle)
}

fun Fragment.pop() {
    findNavController().popBackStack()
}

fun Fragment.onBackPress(unit: () -> Unit) {
    requireActivity().onBackPressedDispatcher.addCallback(this) {
        unit.invoke()
    }
}