package de.valentinho13.catchlingo

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.valentinho13.catchlingo.app.CatchLingoApp
import de.valentinho13.catchlingo.designsystem.CatchLingoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = 0xFFF5F0E8.toInt(),
                darkScrim = 0xFFF5F0E8.toInt(),
            ),
        )
        setContent {
            CatchLingoTheme {
                CatchLingoApp()
            }
        }
    }
}
