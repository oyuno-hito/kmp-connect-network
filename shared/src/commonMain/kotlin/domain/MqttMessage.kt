package domain

import kotlinx.serialization.Serializable

@Serializable
data class MqttMessage (
    val message: String
)