import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.example.bcpnotebook.ui.LoginScreen
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow("BCP Notebook") {
        val navController = rememberNavController()
        LoginScreen(navController)
    }
}
