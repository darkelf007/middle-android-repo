package com.example.androidpracticumcustomview.ui.theme

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch


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
    secondChild: @Composable (() -> Unit)?,
    durationAlphaMillis: Int = 5000,
    durationOffsetMillis: Int = 5000
) {
    require(
        (firstChild == null && secondChild == null) ||
                (firstChild != null && secondChild == null) ||
                (firstChild == null && secondChild != null) ||
                (firstChild != null && secondChild != null)
    ) { "CustomContainerCompose can have maximum 2 children" }

    val alphaAnimation = remember { Animatable(0f) }
    val offsetAnimation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            alphaAnimation.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = durationAlphaMillis, easing = LinearEasing)
            )
        }
        launch {
            offsetAnimation.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = durationOffsetMillis, easing = LinearOutSlowInEasing)
            )
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val containerHeight = constraints.maxHeight.toFloat()
        val firstChildHeight = remember { mutableStateOf(0f) }
        val secondChildHeight = remember { mutableStateOf(0f) }

        firstChild?.let { content ->
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .onGloballyPositioned { coordinates ->
                        firstChildHeight.value = coordinates.size.height.toFloat()
                    }
                    .graphicsLayer {
                        alpha = alphaAnimation.value
                        val offset = offsetAnimation.value
                        val maxOffset = (containerHeight - firstChildHeight.value) / 2
                        translationY = -maxOffset * offset
                    }
            ) {
                ErrorBoundary(content = content)
            }
        }

        secondChild?.let { content ->
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .onGloballyPositioned { coordinates ->
                        secondChildHeight.value = coordinates.size.height.toFloat()
                    }
                    .graphicsLayer {
                        alpha = alphaAnimation.value
                        val offset = offsetAnimation.value
                        val maxOffset = (containerHeight - secondChildHeight.value) / 2
                        translationY = maxOffset * offset
                    }
            ) {
                ErrorBoundary(content = content)
            }
        }
    }
}

@Composable
private fun ErrorBoundary(content: @Composable () -> Unit) {
    val errorState = remember { mutableStateOf<Throwable?>(null) }


    val errorHandler = remember {
        Thread.UncaughtExceptionHandler { _, e ->
            Log.e("CustomContainer", "Render error", e)
            errorState.value = e
        }
    }

    LaunchedEffect(Unit) {
        Thread.setDefaultUncaughtExceptionHandler(errorHandler)
    }

    if (errorState.value != null) {
        Text(
            text = "Render error: ${errorState.value?.message}",
            color = Color.Red
        )
    } else {
        content()
    }
}