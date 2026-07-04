package de.valentinho13.catchlingo.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Erster echter Compose-UI-Test des Projekts. Prüft den Interaktionsvertrag der
 * CatchLingoSpecimenCard, wie sie der Catch-Endzustand in DiscoverScreen nutzt:
 *
 * 1. Ein Tap auf den Pronounce-Button darf onTap (Loslassen der Karte) NICHT auslösen —
 *    sonst würde das Anhören der Aussprache die gehaltene Karte sofort schließen.
 * 2. Die gehaltene Karte trägt die contentDescription "Fund festgehalten: …", damit
 *    TalkBack-Nutzer den Catch-Endzustand wahrnehmen und gezielt loslassen können.
 * 3. Ein Tap auf den Kartenkörper löst onTap genau einmal aus (keine Doppel-Auslösung,
 *    die zu doppelter Haptik oder doppelten State-Resets führen würde).
 */
@RunWith(AndroidJUnit4::class)
class CatchLingoSpecimenCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun pronounceButtonClick_doesNotTriggerOnTap() {
        var tapCount = 0
        var pronounceCount = 0
        composeTestRule.setContent {
            CatchLingoSpecimenCard(
                word = "die Tasse",
                source = "the cup",
                context = "Küche",
                status = "Neu im Journal",
                onPronounceClick = { pronounceCount++ },
                onTap = { tapCount++ },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        composeTestRule.onNodeWithContentDescription("Aussprache anhören").performClick()

        assertEquals("Pronounce-Tap muss beim Button ankommen", 1, pronounceCount)
        assertEquals("Pronounce-Tap darf die Karte nicht loslassen", 0, tapCount)
    }

    @Test
    fun heldCard_exposesSpecimenContentDescription_andReleasesOnCardTap() {
        var tapCount = 0
        val description = "Fund festgehalten: die Tasse. Zum Weiterentdecken antippen."
        composeTestRule.setContent {
            CatchLingoSpecimenCard(
                word = "die Tasse",
                source = "the cup",
                context = "Küche",
                status = "Neu im Journal",
                onTap = { tapCount++ },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = description },
            )
        }

        composeTestRule
            .onNodeWithContentDescription(description)
            .performClick()

        assertEquals("Tap auf den Kartenkörper muss genau einmal loslassen", 1, tapCount)
    }

    @Test
    fun heldCard_showsOnlySpecimenFacts_noDebugTexts() {
        composeTestRule.setContent {
            CatchLingoSpecimenCard(
                word = "die Tasse",
                source = "the cup",
                context = "Küche",
                status = "Neu im Journal",
                onTap = {},
                modifier = Modifier.fillMaxWidth(),
            )
        }

        composeTestRule.onNodeWithText("die Tasse").assertExists()
        composeTestRule.onNodeWithText("the cup").assertExists()
        composeTestRule.onNodeWithText("Küche").assertExists()
        composeTestRule.onNodeWithText("Neu im Journal").assertExists()
        // Alte CatchConfirmationCard-Texte dürfen nicht mehr auftauchen.
        composeTestRule.onAllNodesWithText("Gesammelt").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("Neu").assertCountEquals(0)
    }
}
