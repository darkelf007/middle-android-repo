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
import com.example.androidpracticumcustomview.R

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
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var animationDuration: Long = 2000L
    private var offsetDuration: Long = 5000L

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CustomContainer, defStyleAttr, 0).apply {
            try {
                animationDuration = getInteger(R.styleable.CustomContainer_animationDuration, 2000).toLong()
                offsetDuration = getInteger(R.styleable.CustomContainer_offsetDuration, 5000).toLong()
            } finally {
                recycle()
            }
        }
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            try {
                val child = getChildAt(i)
                val childWidthSpec = MeasureSpec.makeMeasureSpec(
                    MeasureSpec.getSize(widthMeasureSpec) / 2, MeasureSpec.AT_MOST
                )
                val childHeightSpec = MeasureSpec.makeMeasureSpec(
                    MeasureSpec.getSize(heightMeasureSpec) / 2, MeasureSpec.AT_MOST
                )
                child.measure(childWidthSpec, childHeightSpec)
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
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
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
            if (view.width == 0 || view.height == 0 || width == 0 || height == 0) {
                return
            }

            val initialTop = (height - view.height) / 2
            val finalTop = if (isSecond) height - view.height else 0
            val toY = (finalTop - initialTop).toFloat()
            val animationSet = AnimationSet(true).apply {
                addAnimation(AlphaAnimation(0f, 1f).apply {
                    duration = animationDuration
                })

                addAnimation(TranslateAnimation(
                    0f, 0f, 0f, toY
                ).apply {
                    duration = offsetDuration
                    interpolator = AccelerateDecelerateInterpolator()
                })

                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        view.visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        view.layout(
                            (width - view.width) / 2,
                            finalTop,
                            (width + view.width) / 2,
                            finalTop + view.height
                        )
                        view.clearAnimation()
                    }

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

