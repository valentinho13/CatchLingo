package de.valentinho13.catchlingo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.valentinho13.catchlingo.app.CatchLingoApp
import de.valentinho13.catchlingo.designsystem.CatchLingoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CatchLingoTheme {
                CatchLingoApp()
            }
        }
    }
}
