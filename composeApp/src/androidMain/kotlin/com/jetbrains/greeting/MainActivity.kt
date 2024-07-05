package com.jetbrains.greeting

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jetbrains.greeting.viewModel.HttpRequestViewModel
import infra.client.Ktor
import infra.repository.HttpRepository

class MainActivity : ComponentActivity() {
    lateinit var httpRepository: HttpRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ktor = Ktor()
        httpRepository = HttpRepository(ktor)

        val getHttpViewModel = HttpRequestViewModel(httpRepository)
        val sendHttpViewModel = HttpRequestViewModel(httpRepository)

        setContent {
            App(
                getHttpViewModel,
                sendHttpViewModel,
            )
        }
    }
}
