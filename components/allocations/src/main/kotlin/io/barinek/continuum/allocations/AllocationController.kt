package io.barinek.continuum.allocations

import com.fasterxml.jackson.databind.ObjectMapper
import io.barinek.continuum.restsupport.BasicHandler
import org.eclipse.jetty.server.Request
import java.util.Arrays.asList
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AllocationController(val mapper: ObjectMapper, val gateway: AllocationDataGateway, val client: ProjectClient) : BasicHandler() {

    override fun handle(s: String, request: Request, httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) {
        post("/allocations", asList("application/json", "application/vnd.appcontinuum.v1+json"), request, httpServletResponse) {
            val allocation = mapper.readValue(request.reader, AllocationInfo::class.java)

            if (projectIsActive(allocation.projectId)) {
                val record = gateway.create(allocation.projectId, allocation.userId, allocation.firstDay, allocation.lastDay)
                mapper.writeValue(httpServletResponse.outputStream, AllocationInfo(record.id, record.projectId, record.userId, record.firstDay, record.lastDay, "allocation info"))
            }
        }
        get("/allocations", asList("application/json", "application/vnd.appcontinuum.v1+json"), request, httpServletResponse) {
            val projectId = request.getParameter("projectId")
            val list = gateway.findBy(projectId.toLong()).map { record ->
                AllocationInfo(record.id, record.projectId, record.userId, record.firstDay, record.lastDay, "allocation info")
            }
            mapper.writeValue(httpServletResponse.outputStream, list)
        }
    }

    private fun projectIsActive(projectId: Long): Boolean {
        val project = client.getProject(projectId)
        return project != null && project.active
    }
}