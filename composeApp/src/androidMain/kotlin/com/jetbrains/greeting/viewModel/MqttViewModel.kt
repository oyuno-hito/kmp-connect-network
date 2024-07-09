package com.jetbrains.greeting.viewModel

import domain.MqttMessage
import infra.client.Mqtt
import infra.repository.MqttRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MqttViewModel {
    private val mqtt: Mqtt by lazy {
        Mqtt(::receiveMessage)
    }
    private val mqttRepository: MqttRepository by lazy {
        MqttRepository(mqtt)
    }

    private val _connectionState: MutableStateFlow<RunningState> =
        MutableStateFlow(RunningState.Initial)
    val connectionState = _connectionState.asStateFlow()

    private val _publishState: MutableStateFlow<LoadingState> =
        MutableStateFlow(LoadingState.Initial)
    val publishState = _publishState.asStateFlow()

    private val _mqttMessage: MutableStateFlow<MqttMessage> =
        MutableStateFlow(MqttMessage(""))
    val mqttMessage = _mqttMessage.asStateFlow()


    // TODO: Scopeの検討
    @OptIn(DelicateCoroutinesApi::class)
    fun receiveMessage(message: MqttMessage) {
        GlobalScope.launch(Dispatchers.IO) {
            _mqttMessage.update {
                message
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun updateConnectionStatus() {
        _connectionState.update {
            when (it) {
                RunningState.Initial -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        mqtt.connect()
                    }
                    RunningState.Running
                }
                RunningState.Running -> {
                    mqtt.disconnect()
                    RunningState.Stop
                }

                RunningState.Stop -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        mqtt.connect()
                    }
                    RunningState.Running
                }
            }
        }
    }

    // TODO: Scopeの検討
    @OptIn(DelicateCoroutinesApi::class)
    fun publish(message: String): Job = GlobalScope.launch(Dispatchers.IO) {
        _publishState.update { _ -> LoadingState.Loading }
        mqttRepository.publish(message)
        _publishState.update { _ ->
            LoadingState.Stable
        }
    }
}
