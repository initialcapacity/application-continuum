package io.barinek.continuum.restsupport

import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair

class RestTemplate {
    fun get(endpoint: String, accept: String, vararg pairs: BasicNameValuePair) = execute {
        val builder = URIBuilder(endpoint)
        pairs.forEach { pair -> builder.addParameter(pair.name, pair.value) }
        HttpGet(builder.build()).apply {
            addHeader("Accept", accept)
        }
    }

    fun post(endpoint: String, accept: String, data: String) = execute {
        HttpPost(endpoint).apply {
            addHeader("Accept", accept)
            addHeader("Content-type", "application/json")
            entity = StringEntity(data)
        }
    }

    private fun execute(block: () -> HttpUriRequest): String {
        return HttpClients.createDefault().execute(block(), BasicResponseHandler())
    }
}