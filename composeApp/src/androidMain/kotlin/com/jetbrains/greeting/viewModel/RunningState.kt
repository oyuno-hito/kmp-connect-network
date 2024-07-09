package com.jetbrains.greeting.viewModel

sealed class RunningState {
    data object Initial : RunningState()
    data object Stop : RunningState()
    data object Running : RunningState()
}