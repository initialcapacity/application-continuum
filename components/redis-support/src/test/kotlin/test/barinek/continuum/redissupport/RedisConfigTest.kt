package test.barinek.continuum.redissupport

import io.barinek.continuum.redissupport.RedisConfig
import org.junit.Test
import kotlin.test.assertEquals

class RedisConfigTest() {
    val pool = RedisConfig().getPool("discovery")

    @Test
    fun testFind() {
        val jedis = pool.resource
        jedis.set("aKey", "aValue")
        assertEquals("aValue", jedis.get("aKey"))
    }
}