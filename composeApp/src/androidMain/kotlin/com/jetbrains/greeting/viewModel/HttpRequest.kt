package com.jetbrains.greeting.viewModel

import androidx.lifecycle.ViewModel
import com.jetbrains.greeting.viewModel.HttpRequest.Initial
import com.jetbrains.greeting.viewModel.HttpRequest.Loading
import com.jetbrains.greeting.viewModel.HttpRequest.Stable
import domain.HttpCondition
import infra.repository.HttpRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HttpRequestViewModel(
    private val httpRepository: HttpRepository
) : ViewModel() {
    private val _state: MutableStateFlow<HttpRequest> = MutableStateFlow(Initial)
    val state = _state.asStateFlow()

    // TODO: Scopeの検討
    @OptIn(DelicateCoroutinesApi::class)
    fun receiveHttp(): Job = GlobalScope.launch(Dispatchers.IO) {
        _state.update { _ -> Loading }
        val condition = httpRepository.get()
        _state.update { _ ->
            Stable.Condition(
                condition
            )
        }
    }

    // TODO: Scopeの検討
    @OptIn(DelicateCoroutinesApi::class)
    fun sendHttp(): Job = GlobalScope.launch(Dispatchers.IO) {
        _state.update { _ -> Loading }
        val condition = httpRepository.post()
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