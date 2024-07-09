package com.jetbrains.greeting.viewModel

sealed class LoadingState {
    data object Initial : LoadingState()
    data object Loading : LoadingState()
    data object Stable : LoadingState()
}