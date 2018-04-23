package io.barinek.continuum.allocations

import com.fasterxml.jackson.databind.ObjectMapper
import io.barinek.continuum.circuitbreaker.CircuitBreaker
import io.barinek.continuum.discovery.DiscoveryClient
import io.barinek.continuum.restsupport.RestTemplate
import org.apache.http.message.BasicNameValuePair

open class ProjectClient(val mapper: ObjectMapper, val template: RestTemplate) {
    private val circuitBreaker = CircuitBreaker()

    open fun getProject(projectId: Long): ProjectInfo? {
        val endpoint = DiscoveryClient(mapper, template).getUrl("registration") ?: return null

        return circuitBreaker.withCircuitBreaker({
            val response = template.get("$endpoint/project", "application/json", BasicNameValuePair("projectId", projectId.toString()))

            if (!response.isBlank()) mapper.readValue(response, ProjectInfo::class.java) else null

        }, fallback())
    }

    private fun fallback() = { null }
}