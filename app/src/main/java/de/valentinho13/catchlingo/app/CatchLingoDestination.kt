package de.valentinho13.catchlingo.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.ui.graphics.vector.ImageVector

enum class CatchLingoDestination(
    val label: String,
    val icon: ImageVector,
) {
    Discover("Entdecken", Icons.Outlined.Explore),
    Dictionary("Wörterbuch", Icons.Outlined.Book),
    Review("Wiederholen", Icons.Outlined.Spa),
}
