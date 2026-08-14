package test.barinek.continuum.circuitbreaker

import io.barinek.continuum.circuitbreaker.CircuitBreaker
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class CircuitBreakerTest {
    @Test
    fun testClosed() {
        val circuitBreaker = CircuitBreaker(100)

        val actual = circuitBreaker.withCircuitBreaker(
            { Thread.sleep(10); true }, { false })
        assertTrue(actual)
    }

    @Test
    fun testTimeout() {
        val circuitBreaker = CircuitBreaker(10)

        val actual = circuitBreaker.withCircuitBreaker(
            { Thread.sleep(100); true }, { false })
        assertFalse(actual)
    }

    @Test
    fun testMaxFailures() {
        val circuitBreaker = CircuitBreaker(0, 3)

        for (i in 1..3) {
            val actual = circuitBreaker.withCircuitBreaker(
                { Thread.sleep(100); true }, { false })
            assertFalse(actual)
        }

        val actual = circuitBreaker.withCircuitBreaker(
            { fail(); Thread.sleep(0); true }, { false })
        assertFalse(actual)
    }

    @Test
    fun testBoundsWorkerThreadsRegardlessOfCallCount() {
        val maxConcurrentCalls = 4
        val circuitBreaker = CircuitBreaker(1000, 3, 300, maxConcurrentCalls)
        val threadsUsed = ConcurrentHashMap.newKeySet<Thread>()

        for (i in 1..50) {
            val actual = circuitBreaker.withCircuitBreaker(
                { threadsUsed.add(Thread.currentThread()); true }, { false })
            assertTrue(actual)
        }

        assertTrue("used ${threadsUsed.size} threads for 50 calls", threadsUsed.size <= maxConcurrentCalls)
    }

    @Test
    fun testReleasesTheWorkerWhenACallTimesOut() {
        val circuitBreaker = CircuitBreaker(50, 100, 300, 1)
        val interrupted = CountDownLatch(1)

        val actual = circuitBreaker.withCircuitBreaker({
            try {
                Thread.sleep(5000)
                false
            } catch (e: InterruptedException) {
                interrupted.countDown()
                false
            }
        }, { true })

        assertTrue(actual)
        assertTrue("timed out work was left running", interrupted.await(2, TimeUnit.SECONDS))
    }

    @Test
    fun testCapsThreadsWhenCallsOverlap() {
        val maxConcurrentCalls = 2
        val circuitBreaker = CircuitBreaker(2000, 100, 300, maxConcurrentCalls)
        val threadsUsed = ConcurrentHashMap.newKeySet<Thread>()
        val release = CountDownLatch(1)
        val submitted = CountDownLatch(8)

        val callers = (1..8).map {
            Thread {
                circuitBreaker.withCircuitBreaker(
                    { threadsUsed.add(Thread.currentThread()); release.await(2, TimeUnit.SECONDS) }, { false })
            }.apply { isDaemon = true; start(); submitted.countDown() }
        }
        submitted.await(2, TimeUnit.SECONDS)
        Thread.sleep(200)
        release.countDown()
        callers.forEach { it.join(5000) }

        assertTrue("used ${threadsUsed.size} threads for 8 overlapping calls", threadsUsed.size <= maxConcurrentCalls)
    }

    @Test
    fun testWorkerThreadsDoNotKeepTheJvmAlive() {
        val circuitBreaker = CircuitBreaker(1000)

        val actual = circuitBreaker.withCircuitBreaker(
            { Thread.currentThread().isDaemon }, { false })

        assertTrue(actual)
    }

    @Test
    fun testReset() {
        val circuitBreaker = CircuitBreaker(10, 3, 300)

        for (i in 1..3) {
            val actual = circuitBreaker.withCircuitBreaker(
                { Thread.sleep(100); true }, { false })
            assertFalse(actual)
        }

        Thread.sleep(400)

        val actual = circuitBreaker.withCircuitBreaker(
            { Thread.sleep(0); true }, { false })
        assertTrue(actual)
    }
}
