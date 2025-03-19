package com.example.androidpracticumcustomview.ui.theme

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AlphaAnimation
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
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    init {
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            val childLeft = (width - childWidth) / 2
            val childTop = if (i == 0) 0 else height - childHeight

            child.layout(
                childLeft,
                childTop,
                childLeft + childWidth,
                childTop + childHeight
            )
        }
    }

    override fun addView(child: View) {
        if (childCount >= 2) throw IllegalStateException("Cannot add more than two children")
        child.visibility = View.INVISIBLE
        super.addView(child)
        animateChild(child, childCount == 1)
    }


    private fun animateChild(view: View, isSecond: Boolean) {
        val alphaAnimation = AlphaAnimation(0f, 1f).apply {
            duration = 2000
            fillAfter = true
        }

        val translateAnimation = TranslateAnimation(
            0f, 0f, height / 2f, if (isSecond) height / 2f else -height / 2.1f
        ).apply {
            duration = 5000
            fillAfter = true
        }

        view.post {
            view.visibility = View.VISIBLE
            view.startAnimation(alphaAnimation)
            view.startAnimation(translateAnimation)
        }
    }
}