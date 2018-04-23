package io.barinek.continuum.redissupport

import com.fasterxml.jackson.databind.ObjectMapper
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

open class RedisConfig {
    fun getPool(name: String): JedisPool {
        val json = System.getenv("VCAP_SERVICES")
        val info = from(name, json)!!
        return JedisPool(JedisPoolConfig(), info.host, info.port, 2000, info.password);
    }

    fun from(name: String, json: String): RedisInfo? {
        val mapper = ObjectMapper()
        val root = mapper.readTree(json)
        root.findValue("rediscloud").forEach { service ->
            val node = service.findValue("name")
            if (node.textValue() == name) {
                val credentials = service.findValue("credentials")
                val host = credentials.findValue("hostname").textValue()
                val port = credentials.findValue("port").intValue()
                val password = credentials.findValue("password").textValue()
                return RedisInfo(host, port, password)
            }
        }
        return null
    }

    class RedisInfo(val host: String, val port: Int, val password: String)
}