package com.example.myapplication

import android.view.View
import android.view.animation.TranslateAnimation
import androidx.interpolator.view.animation.FastOutSlowInInterpolator


fun View.slideTo(endX: Float, endY: Float, animTime: Long) {
    val slideTo = TranslateAnimation(0f, endX - x, 0f, endY - y).apply {
        duration = animTime
        interpolator = FastOutSlowInInterpolator()
    }
    startAnimation(slideTo)
}

fun View.slideFromOut(animTime: Long) {
    val slideFromOut = TranslateAnimation(0f, 0f, 1000f, y-1200).apply {
        duration = animTime
        interpolator = FastOutSlowInInterpolator()
    }

    startAnimation(slideFromOut)
}

fun animateAllViews(views: List<View?>, targetViews: List<View?>, animTime: Long) {
    var i =0
    for (view in views) {
        view?.slideTo(targetViews[i]!!.x, targetViews[i]!!.y, animTime)
        i++
    }
}
