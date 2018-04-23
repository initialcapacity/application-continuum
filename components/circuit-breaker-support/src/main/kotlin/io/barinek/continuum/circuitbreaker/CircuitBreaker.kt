package io.barinek.continuum.circuitbreaker

import java.lang.System.currentTimeMillis
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CircuitBreaker(private val timeoutInMillis: Long = 200, private val maxFailures: Int = 3, private val retryIntervalInMillis: Long = 300) {
    private var currentFailures: Int = 0
    private var lastFailureInMillis: Long = 0

    private fun open() = currentFailures >= maxFailures
    private fun shouldRetry() = (lastFailureInMillis + retryIntervalInMillis) < currentTimeMillis()

    fun <T> withCircuitBreaker(function: () -> T, fallback: () -> T): T {

        if (open() && !shouldRetry()) return fallback()

        val future = Executors.newSingleThreadExecutor().submit(function)

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
}