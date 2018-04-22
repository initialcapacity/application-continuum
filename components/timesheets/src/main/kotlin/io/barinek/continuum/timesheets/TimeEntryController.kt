package io.barinek.continuum.timesheets

import com.fasterxml.jackson.databind.ObjectMapper
import io.barinek.continuum.restsupport.BasicHandler
import org.eclipse.jetty.server.Request
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class TimeEntryController(val mapper: ObjectMapper, val gateway: TimeEntryDataGateway, val client: ProjectClient) : BasicHandler() {

    override fun handle(s: String, request: Request, httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) {
        post("/time-entries", request, httpServletResponse) {
            val entry = mapper.readValue(request.reader, TimeEntryInfo::class.java)

            if (projectIsActive(entry.projectId)) {
                val record = gateway.create(entry.projectId, entry.userId, entry.date, entry.hours)
                mapper.writeValue(httpServletResponse.outputStream, TimeEntryInfo(record.id, record.projectId, record.userId, record.date, record.hours, "entry info"))
            }
        }
        get("/time-entries", request, httpServletResponse) {
            val userId = request.getParameter("userId")
            val list = gateway.findBy(userId.toLong()).map { record ->
                TimeEntryInfo(record.id, record.projectId, record.userId, record.date, record.hours, "entry info")
            }
            mapper.writeValue(httpServletResponse.outputStream, list)
        }
    }

    private fun projectIsActive(projectId: Long): Boolean {
        val project = client.getProject(projectId)
        return project != null && project.active
    }
}