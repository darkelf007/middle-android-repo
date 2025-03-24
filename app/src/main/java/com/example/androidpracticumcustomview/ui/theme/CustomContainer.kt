package com.example.androidpracticumcustomview.ui.theme

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout

/*
Задание:
Реализуйте необходимые компоненты;
Создайте проверку что дочерних элементов не более 2-х;
Предусмотрите обработку ошибок рендера дочерних элементов.
Задание по желанию:
Предусмотрите параметризацию длительности анимации.
 */

class CustomContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    private val animationDuration: Long = 5000L,
    private val offsetDuration: Long = 5000L
) : FrameLayout(context, attrs) {

    init {
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            try {
                measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec)
            } catch (e: Exception) {
                handleChildError(e, "Error measuring child $i")
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            val childLeft = (width - childWidth) / 2
            val childTop = (height - childHeight) / 2
            child.layout(
                childLeft, childTop, childLeft + childWidth, childTop + childHeight
            )
        }

        if (childCount == 2) {
            post {
                for (i in 0 until childCount) {
                    val child = getChildAt(i)
                    if (child.visibility == View.INVISIBLE) {
                        animateChild(child, i == 1)
                    }
                }
            }
        }
    }

    override fun addView(child: View) {
        if (childCount >= 2) throw IllegalStateException("Cannot add more than two children")
        try {
            child.visibility = View.INVISIBLE
            super.addView(child)
        } catch (e: Exception) {
            handleChildError(e, "Error adding child")
            throw e
        }
    }

    private fun animateChild(view: View, isSecond: Boolean) {
        try {
            val animationSet = AnimationSet(true).apply {
                addAnimation(AlphaAnimation(0f, 1f).apply {
                    duration = animationDuration
                })

                val fromY = 0f
                val toY = if (isSecond) {
                    (height / 2 + view.height).toFloat()
                } else {
                    -(height / 2 + view.height).toFloat()
                }

                addAnimation(TranslateAnimation(
                    0f, 0f, fromY, toY
                ).apply {
                    duration = offsetDuration
                    interpolator = AccelerateDecelerateInterpolator()
                })

                fillAfter = true
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        view.visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {}
                })
            }

            view.startAnimation(animationSet)
        } catch (e: Exception) {
            handleChildError(e, "Error animating child")
        }

    }

    private fun handleChildError(e: Exception, message: String) {
        Log.e("CustomContainer", message, e)
    }
}

