package de.valentinho13.catchlingo.core.text

import org.junit.Assert.assertEquals
import org.junit.Test

class LabelNormalizerTest {

    @Test
    fun trimsLowercasesAndCollapsesWhitespace() {
        assertEquals("coffee cup", LabelNormalizer.normalize("  Coffee   Cup "))
    }

    @Test
    fun variantsOfSameLabelNormalizeEqual() {
        assertEquals(LabelNormalizer.normalize("Cup"), LabelNormalizer.normalize("cup "))
    }
}
