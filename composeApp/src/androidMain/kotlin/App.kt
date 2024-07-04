import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.jetbrains.greeting.MainActivity
import com.jetbrains.greeting.viewModel.HttpRequest
import com.jetbrains.greeting.viewModel.HttpRequestViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    getHttpViewModel: HttpRequestViewModel = HttpRequestViewModel(),
    sendHttpViewModel: HttpRequestViewModel = HttpRequestViewModel()
) {
    MaterialTheme {
        val activity = LocalContext.current as MainActivity
        val getHttpState by getHttpViewModel.state.collectAsState()
        val sendHttpState by sendHttpViewModel.state.collectAsState()

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = {
                    sendHttpViewModel.sendHttp(activity.ktor)
                },
                enabled = sendHttpState !is HttpRequest.Loading
            ) {
                Text("HTTPリクエスト送信")
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
                    getHttpViewModel.receiveHttp(activity.ktor)
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
        }
    }
}
