package test.barinek.continuum.discovery

import io.barinek.continuum.discovery.InstanceDataGateway
import io.barinek.continuum.redissupport.RedisConfig
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class InstanceDataGatewayTest() {
    val pool = RedisConfig().getPool("discovery")

    @Before
    fun cleanDatabase() {
        pool.resource.flushAll()
    }

    @Test
    fun testHeartbeat() {
        val gateway = InstanceDataGateway(pool, 5000L)

        val instance = gateway.heartbeat("allocations", "http://localhost:8081")

        assertEquals("allocations", instance.appId)
        assertEquals("http://localhost:8081", instance.url)
    }

    @Test
    fun testFindBy() {
        pool.resource.psetex("allocations:http://localhost:8081", 5000L, "http://localhost:8081")

        val gateway = InstanceDataGateway(pool, 5000L)

        val instance = gateway.findBy("allocations").first()

        assertEquals("allocations", instance.appId)
        assertEquals("http://localhost:8081", instance.url)
    }

    @Test
    fun testExpired() {
        val gateway = InstanceDataGateway(pool, 5L)

        gateway.heartbeat("allocations", "http://localhost:8081")

        Thread.sleep(10)

        assertEquals(0, gateway.findBy("allocations").size)
    }
}