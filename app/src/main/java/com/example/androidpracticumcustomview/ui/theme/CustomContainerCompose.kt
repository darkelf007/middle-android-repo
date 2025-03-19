package com.example.androidpracticumcustomview.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset


/*
Задание:
Реализуйте необходимые компоненты;
Создайте проверку что дочерних элементов не более 2-х;
Предусмотрите обработку ошибок рендера дочерних элементов.
Задание по желанию:
Предусмотрите параметризацию длительности анимации.
 */
@Composable
fun CustomContainerCompose(
    firstChild: @Composable (() -> Unit)?,
    secondChild: @Composable (() -> Unit)?
) {
    if (listOfNotNull(firstChild, secondChild).size > 2) {
        throw IllegalStateException("Cannot have more than two children")
    }

    val alphaAnimation = remember { Animatable(0f) }
    val offsetAnimation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(2000)
        )
        offsetAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(5000, easing = LinearOutSlowInEasing)
        )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val boxHeight = constraints.maxHeight

        firstChild?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .graphicsLayer(alpha = alphaAnimation.value)
                    .offset {
                        IntOffset(
                            x = 0,
                            y = ((-offsetAnimation.value * boxHeight) / 2).toInt()
                        )
                    }
            ) {
                it()
            }
        }

        secondChild?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .graphicsLayer(alpha = alphaAnimation.value)
                    .offset {
                        IntOffset(
                            x = 0,
                            y = ((offsetAnimation.value * boxHeight) / 2).toInt()
                        )
                    }
            ) {
                it()
            }
        }
    }
}