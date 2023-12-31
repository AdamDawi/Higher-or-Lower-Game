package com.example.myapplication

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.res.Resources
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout

object SlideAnimation
{
    fun onStartAnimation(llUp: LinearLayout, llDown: LinearLayout, llDuplicate: LinearLayout)
    {

        val locationUp = IntArray(2)
        //getting location of country Up
        llUp.getLocationOnScreen(locationUp)

        //getting screen metrics
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val percentOfScreen = 0.03 // 3%
        val translationY = percentOfScreen * screenHeight


        val countryDownAnimator = ValueAnimator.ofFloat(0f, -locationUp[1].toFloat()-llDown.height.toFloat()-translationY.toFloat())

        countryDownAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            llDown.translationY = value
            llUp.translationY = value
        }

        val countryDuplicateAnimator = ValueAnimator.ofFloat(llDuplicate.height.toFloat()+locationUp[1].toFloat(), 0f)

        countryDuplicateAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            llDuplicate.translationY = value
        }

        val animatorSet = AnimatorSet()

        animatorSet.play(countryDownAnimator).with(countryDuplicateAnimator)

        animatorSet.interpolator = LinearInterpolator()
        animatorSet.duration = Constants.NEXT_TURN_ANIM_DELAY
        animatorSet.start()
    }

    fun reset(llUp: LinearLayout, llDown: LinearLayout, llDuplicate: LinearLayout)
    {
        val animator = ValueAnimator.ofFloat(0f, 0f)

        animator.addUpdateListener {
            llUp.translationY = 0f
            llDown.translationY = 0f
            llDuplicate.translationY = 0f
        }

        animator.start()
    }
}