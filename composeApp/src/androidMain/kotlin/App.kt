import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jetbrains.greeting.viewModel.HttpRequest
import com.jetbrains.greeting.viewModel.HttpRequestViewModel
import com.jetbrains.greeting.viewModel.LoadingState
import com.jetbrains.greeting.viewModel.MqttViewModel
import com.jetbrains.greeting.viewModel.RunningState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    getHttpViewModel: HttpRequestViewModel,
    sendHttpViewModel: HttpRequestViewModel,
    mqttViewModel: MqttViewModel,
) {
    MaterialTheme {
        val getHttpState by getHttpViewModel.state.collectAsState()
        val sendHttpState by sendHttpViewModel.state.collectAsState()
        val mqttConnectionState by mqttViewModel.connectionState.collectAsState()
        val mqttPublishState by mqttViewModel.publishState.collectAsState()
        val receiveMqttMessage by mqttViewModel.mqttMessage.collectAsState()

        var pubText by remember { mutableStateOf("") }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("HTTP")
            Button(
                onClick = {
                    sendHttpViewModel.sendHttp()
                },
                enabled = sendHttpState !is HttpRequest.Loading
            ) {
                Text("リクエスト送信")
            }
            when (val state = sendHttpState) {
                HttpRequest.Initial -> Text("未送信")
                HttpRequest.Loading -> Text("送信中")
                is HttpRequest.Stable.Condition -> {
                    Text("ステータスコード: ${state.condition.statusCode}")
                    Text("時刻: ${state.condition.time}")
                }
            }
            Button(
                onClick = {
                    getHttpViewModel.receiveHttp()
                },
                enabled = getHttpState !is HttpRequest.Loading
            ) {
                Text("HTTPリクエスト受信")
            }
            when (val state = getHttpState) {
                HttpRequest.Initial -> Text("リクエスト未実施")
                HttpRequest.Loading -> Text("受信中")
                is HttpRequest.Stable.Condition -> {
                    Text("ステータスコード: ${state.condition.statusCode}")
                    Text("時刻: ${state.condition.time}")
                }
            }

            Spacer(
                Modifier
                    .background(Color.DarkGray)
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .height(1.dp)
            )

            Text("MQTT")

            Button(
                onClick = {
                    mqttViewModel.updateConnectionStatus()
                }
            ) {
                when (mqttConnectionState) {
                    RunningState.Initial -> Text("通信開始")
                    RunningState.Stop -> Text("通信再開")
                    RunningState.Running -> Text("通信中断")
                }
            }

            TextField(
                value = pubText,
                onValueChange = { pubText = it },
                label = { Text("送信メッセージ") })
            Button(
                onClick = {
                    mqttViewModel.publish(pubText)
                    pubText = ""
                },
                enabled = mqttPublishState !is LoadingState.Loading && mqttConnectionState is RunningState.Running
            ) {
                if (mqttPublishState !is LoadingState.Loading)
                    Text("送信") else Text("送信中")
            }


            Text("受信メッセージ")
            if (mqttConnectionState is RunningState.Initial)
                Text("受信開始後この値が更新されます") else Text(receiveMqttMessage.message)

            Spacer(
                Modifier
                    .background(Color.DarkGray)
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .height(1.dp)
            )
        }
    }
}
