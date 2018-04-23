package io.barinek.continuum.discovery

import com.fasterxml.jackson.databind.ObjectMapper
import io.barinek.continuum.restsupport.BasicHandler
import org.eclipse.jetty.server.Request
import org.slf4j.LoggerFactory
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class DiscoveryController(val mapper: ObjectMapper, val gateway: InstanceDataGateway) : BasicHandler() {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handle(s: String, request: Request, httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) {
        post("/discovery/apps", Arrays.asList("application/json"), request, httpServletResponse) {
            val instance = mapper.readValue(request.reader, InstanceInfo::class.java)
            val record = gateway.heartbeat(instance.appId, instance.url)
            logger.info("Registered app ${instance.appId}, ${instance.url}")
            mapper.writeValue(httpServletResponse.outputStream, InstanceInfo(record.appId, record.url));
        }

        get("/discovery/apps", Arrays.asList("application/json"), request, httpServletResponse) {
            val appId = request.getParameter("appId")
            val list = gateway.findBy(appId).map { record ->
                InstanceInfo(record.appId, record.url)
            }
            mapper.writeValue(httpServletResponse.outputStream, list)
        }
    }
}