package io.barinek.continuum.discovery

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.barinek.continuum.restsupport.RestTemplate
import org.apache.http.message.BasicNameValuePair
import org.slf4j.LoggerFactory
import java.util.*

open class DiscoveryClient(val mapper: ObjectMapper, val template: RestTemplate) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private fun <E> List<E>.random(random: java.util.Random): E? = if (size > 0) get(random.nextInt(size)) else null

    fun getUrl(appId: String): String? {
        val endpoint = System.getenv("DISCOVERY_SERVER_ENDPOINT")
        val response = template.get("$endpoint/discovery/apps", "application/json", BasicNameValuePair("appId", appId))
        val instances: List<InstanceInfo> = mapper.readValue(response, object : TypeReference<List<InstanceInfo>>() {})

        logger.info("Found ${instances.size} instance(s) of $appId.")

        return when {
            instances.isEmpty() -> null
            else -> instances.random(Random())!!.url
        }
    }

    fun heartbeat(appId: String, url: String) {
        val endpoint = System.getenv("DISCOVERY_SERVER_ENDPOINT")
        try {
            val data = "{\"appId\":\"$appId\",\"url\":\"$url\"}"
            RestTemplate().post("$endpoint/discovery/apps", "application/json", data)
        } catch (e: Exception) {
            logger.error("Unable to contact discovery server. $endpoint", e)
        }
    }

    private data class InstanceInfo(val appId: String, val url: String)
}