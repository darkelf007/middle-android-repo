package com.example.androidpracticumcustomview.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

/*
Задание:
Реализуйте необходимые компоненты.
*/

@Composable
fun MainScreen(closeActivity: () -> Unit) {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable { closeActivity.invoke() },
            contentAlignment = Alignment.Center
        ) {

            CustomContainerCompose(
                firstChild = {
                    Text(
                        text = "Первый элемент",
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                },
                secondChild = {
                    Text(
                        text = "Второй элемент",
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
            )
        }
    }
}