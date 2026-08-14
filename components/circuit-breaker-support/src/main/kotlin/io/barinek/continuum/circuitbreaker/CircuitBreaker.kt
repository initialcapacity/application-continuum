package io.barinek.continuum.circuitbreaker

import java.lang.System.currentTimeMillis
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class CircuitBreaker(
        private val timeoutInMillis: Long = 200,
        private val maxFailures: Int = 3,
        private val retryIntervalInMillis: Long = 300,
        maxConcurrentCalls: Int = 10
) {
    private var currentFailures: Int = 0
    private var lastFailureInMillis: Long = 0
    private val executor = ThreadPoolExecutor(
            0, maxConcurrentCalls, idleWorkerTimeoutInSeconds, TimeUnit.SECONDS, SynchronousQueue(), daemonThreadFactory())

    private fun open() = currentFailures >= maxFailures
    private fun shouldRetry() = (lastFailureInMillis + retryIntervalInMillis) < currentTimeMillis()

    fun <T> withCircuitBreaker(function: () -> T, fallback: () -> T): T {

        if (open() && !shouldRetry()) return fallback()

        val future = executor.submit(function)

        return try {
            future.get(timeoutInMillis, TimeUnit.MILLISECONDS).apply {
                reset()
            }
        } catch (e: Exception) {
            fail()
            fallback()
        } finally {
            future.cancel(true)
        }
    }

    private fun fail() {
        lastFailureInMillis = currentTimeMillis()
        currentFailures++
    }

    private fun reset() {
        currentFailures = 0
    }

    private fun daemonThreadFactory() = ThreadFactory { runnable ->
        Thread(runnable, "circuit-breaker-${threadCount.incrementAndGet()}").apply { isDaemon = true }
    }

    private companion object {
        const val idleWorkerTimeoutInSeconds = 60L
        val threadCount = AtomicLong()
    }
}
