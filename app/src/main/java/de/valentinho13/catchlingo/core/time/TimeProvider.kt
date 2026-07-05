package de.valentinho13.catchlingo.core.time

/**
 * Injizierbare Zeitquelle, damit Zeitstempel deterministisch testbar sind.
 * Kein verstreutes System.currentTimeMillis() in Domain/Data.
 */
interface TimeProvider {
    fun nowMillis(): Long
}

class SystemTimeProvider : TimeProvider {
    override fun nowMillis(): Long = System.currentTimeMillis()
}
