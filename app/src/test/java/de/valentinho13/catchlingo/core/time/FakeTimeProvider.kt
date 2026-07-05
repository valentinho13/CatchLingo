package de.valentinho13.catchlingo.core.time

/** Deterministische Zeitquelle für Tests. `now` kann zwischen Aufrufen verstellt werden. */
class FakeTimeProvider(var now: Long = 1_000L) : TimeProvider {
    override fun nowMillis(): Long = now
}
