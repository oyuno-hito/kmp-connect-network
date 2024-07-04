package com.jetbrains.greeting.viewModel

import androidx.lifecycle.ViewModel
import com.jetbrains.greeting.viewModel.HttpRequest.Initial
import com.jetbrains.greeting.viewModel.HttpRequest.Loading
import com.jetbrains.greeting.viewModel.HttpRequest.Stable
import domain.HttpCondition
import infra.http.Ktor
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HttpRequestViewModel : ViewModel() {
    private val _state: MutableStateFlow<HttpRequest> = MutableStateFlow(Initial)
    val state = _state.asStateFlow()

    // TODO: Scopeの検討
    @OptIn(DelicateCoroutinesApi::class)
    fun receiveHttp(ktor: Ktor): Job = GlobalScope.launch(Dispatchers.IO) {
        _state.update { _ -> Loading }
        val condition = ktor.get()
        _state.update { _ ->
            Stable.Condition(
                condition
            )
        }
    }

    // TODO: Scopeの検討
    @OptIn(DelicateCoroutinesApi::class)
    fun sendHttp(ktor: Ktor): Job = GlobalScope.launch(Dispatchers.IO) {
        _state.update { _ -> Loading }
        val condition = ktor.post()
        _state.update { _ ->
            Stable.Condition(
                condition
            )
        }
    }
}

sealed class HttpRequest {
    data object Initial : HttpRequest()
    data object Loading : HttpRequest()
    sealed class Stable : HttpRequest() {
        abstract val condition: HttpCondition

        data class Condition(
            override val condition: HttpCondition
        ) : Stable()
    }
}