package infra.repository

import domain.MqttMessage
import infra.client.Mqtt
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mqtt.packets.Qos

class MqttRepository(private val mqtt: Mqtt) {
    @OptIn(ExperimentalUnsignedTypes::class, DelicateCoroutinesApi::class)
    fun publish(message: String) {
        val value = MqttMessage(
            message
        )
        val obj = Json.encodeToString(value)
        GlobalScope.launch(Dispatchers.IO) {
            mqtt.client.publish(false, Qos.EXACTLY_ONCE, "oyuno_hito", obj.encodeToByteArray().toUByteArray())
        }
    }
}