package io.barinek.continuum.discovery

import redis.clients.jedis.JedisPool

class InstanceDataGateway(val pool: JedisPool, val timeToLiveInMillis: Long) {
    fun heartbeat(appId: String, url: String): InstanceRecord {
        val resource = pool.resource
        resource.psetex("$appId:$url", timeToLiveInMillis, url)
        resource.close()
        return InstanceRecord(appId, url)
    }

    fun findBy(appId: String): List<InstanceRecord> {
        val list = mutableListOf<InstanceRecord>()
        val resource = pool.resource
        resource.keys("$appId:*")
                .map { pool.resource.get(it) }
                .mapTo(list) { InstanceRecord(appId, it) }
        resource.close()
        return list
    }
}