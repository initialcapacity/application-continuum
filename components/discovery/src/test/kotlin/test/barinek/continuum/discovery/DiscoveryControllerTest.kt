package test.barinek.continuum.discovery

import com.fasterxml.jackson.core.type.TypeReference
import io.barinek.continuum.discovery.DiscoveryController
import io.barinek.continuum.discovery.InstanceDataGateway
import io.barinek.continuum.discovery.InstanceInfo
import io.barinek.continuum.redissupport.RedisConfig
import io.barinek.continuum.restsupport.BasicApp
import io.barinek.continuum.testsupport.TestControllerSupport
import org.apache.http.message.BasicNameValuePair
import org.eclipse.jetty.server.handler.HandlerList
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class DiscoveryControllerTest : TestControllerSupport() {
    val pool = RedisConfig().getPool("discovery")

    internal var app: BasicApp = object : BasicApp() {
        override fun getPort() = 8081

        override fun handlerList() = HandlerList().apply {
            addHandler(DiscoveryController(mapper, InstanceDataGateway(pool, 5000)))
        }
    }

    @Before
    fun setUp() {
        pool.resource.flushAll()
        app.start()
    }

    @After
    fun tearDown() {
        app.stop()
    }

    @Test
    fun testHeartbeat() {
        val json = "{\"appId\":\"allocations\",\"url\":\"http://localhost:8081\"}"
        val response = template.post("http://localhost:8081/discovery/apps", "application/json", json)
        val actual = mapper.readValue(response, InstanceInfo::class.java)

        assertEquals("allocations", actual.appId)
        assertEquals("http://localhost:8081", actual.url)
    }

    @Test
    fun testFind() {
        val resource = pool.resource

        resource.psetex("allocations:http://localhost:8081", 5000L, "http://localhost:8081")
        resource.psetex("allocations:http://localhost:8082", 5000L, "http://localhost:8083")
        resource.psetex("allocations:http://localhost:8083", 5000L, "http://localhost:8083")

        val response = template.get("http://localhost:8081/discovery/apps", "application/json", BasicNameValuePair("appId", "allocations"))

        val instances: List<InstanceInfo> = mapper.readValue(response, object : TypeReference<List<InstanceInfo>>() {})
        assertEquals(3, instances.size)

        val first = instances.first()
        assertEquals("allocations", first.appId)
        assertEquals("http://localhost:8081", first.url)
    }

    @Test
    fun testMixed() {
        val resource = pool.resource

        resource.psetex("allocations:http://localhost:8081", 5000L, "http://localhost:8081")
        resource.psetex("backlog:http://localhost:8082", 5000L, "http://localhost:8083")
        resource.psetex("timesheets:http://localhost:8083", 5000L, "http://localhost:8083")
        resource.psetex("registration:http://localhost:8084", 5000L, "http://localhost:8084")

        val response = template.get("http://localhost:8081/discovery/apps", "application/json", BasicNameValuePair("appId", "allocations"))

        val instances: List<InstanceInfo> = mapper.readValue(response, object : TypeReference<List<InstanceInfo>>() {})
        assertEquals(1, instances.size)
    }

    @Test
    fun testEmpty() {
        val response = template.get("http://localhost:8081/discovery/apps", "application/json", BasicNameValuePair("appId", "allocations"))

        val instances: List<InstanceInfo> = mapper.readValue(response, object : TypeReference<List<InstanceInfo>>() {})
        assertEquals(0, instances.size)
    }
}