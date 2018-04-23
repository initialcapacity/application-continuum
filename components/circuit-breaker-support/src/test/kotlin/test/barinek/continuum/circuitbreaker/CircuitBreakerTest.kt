package test.barinek.continuum.circuitbreaker

import io.barinek.continuum.circuitbreaker.CircuitBreaker
import org.junit.Assert.*
import org.junit.Test

class CircuitBreakerTest {
    @Test
    fun testClosed() {
        val circuitBreaker = CircuitBreaker(100)

        val actual = circuitBreaker.withCircuitBreaker({ sleepTrue(10) }, { false })
        assertTrue(actual)
    }

    @Test
    fun testTimeout() {
        val circuitBreaker = CircuitBreaker(10)

        val actual = circuitBreaker.withCircuitBreaker({ sleepTrue(100) }, { false })
        assertFalse(actual)
    }

    @Test
    fun testMaxFailures() {
        val circuitBreaker = CircuitBreaker(0, 3)

        for (i in 1..3) {
            val actual = circuitBreaker.withCircuitBreaker({ sleepTrue(100) }, { false })
            assertFalse(actual)
        }

        val actual = circuitBreaker.withCircuitBreaker({ fail(); sleepTrue(0) }, { false })
        assertFalse(actual)
    }

    @Test
    fun testReset() {
        val circuitBreaker = CircuitBreaker(10, 3, 300)

        for (i in 1..3) {
            val actual = circuitBreaker.withCircuitBreaker({ sleepTrue(100) }, { false })
            assertFalse(actual)
        }

        Thread.sleep(400)

        val actual = circuitBreaker.withCircuitBreaker({ sleepTrue(0) }, { false })
        assertTrue(actual)
    }

    private fun sleepTrue(sleepInMillis: Long): Boolean {
        Thread.sleep(sleepInMillis)
        return true
    }
}