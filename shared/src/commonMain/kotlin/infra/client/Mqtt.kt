package infra.client

import MQTTClient
import domain.MqttMessage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import mqtt.MQTTVersion
import mqtt.Subscription
import mqtt.packets.Qos
import mqtt.packets.mqttv5.ReasonCode
import mqtt.packets.mqttv5.SubscriptionOptions

class Mqtt(val callback: (value: MqttMessage) -> Unit) {
    // TODO: Jobの値更新する箇所がない
    private var coroutineJob: Job? = null
    @OptIn(ExperimentalUnsignedTypes::class)
    val client: MQTTClient = MQTTClient(
        MQTTVersion.MQTT5,
        "broker.emqx.io",
        1883,
        null
    ) {
        val rawValue = it.payload?.toByteArray()?.decodeToString() ?: return@MQTTClient

        try {
            val message = Json.decodeFromString<MqttMessage>(rawValue)
            callback(message)
        } catch (e: Exception){
            println(e)
        }


    }



    init {
        client.subscribe(listOf(Subscription("oyuno_hito", SubscriptionOptions(Qos.EXACTLY_ONCE))))
    }

    // TODO: コメントアウトコードと比較検討
    suspend fun connect() = coroutineScope {
        client.run()
    }

//    @OptIn(DelicateCoroutinesApi::class)
//    fun connect()  {
//        coroutineJob = GlobalScope.launch(Dispatchers.IO) {
//            client.run()
//        }
//    }


    // TODO: 中断できていなさそうなので要調査
    fun disconnect() {
        coroutineJob?.cancel()

    }
}