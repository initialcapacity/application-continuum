package io.barinek.continuum.projects

import com.fasterxml.jackson.databind.ObjectMapper
import io.barinek.continuum.restsupport.BasicHandler
import org.eclipse.jetty.server.Request
import java.util.Arrays.asList
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ProjectControllerV1(val mapper: ObjectMapper, val gateway: ProjectDataGateway) : BasicHandler() {

    override fun handle(s: String, request: Request, httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) {
        post("/projects", asList("application/json", "application/vnd.appcontinuum.v1+json"), request, httpServletResponse) {
            val project = mapper.readValue(request.reader, ProjectInfoV1::class.java)
            val record = gateway.create(project.accountId, project.name)
            mapper.writeValue(httpServletResponse.outputStream, ProjectInfoV1(record.id, record.accountId, record.name, record.active, "project info"))
        }
        get("/projects", asList("application/json", "application/vnd.appcontinuum.v1+json"), request, httpServletResponse) {
            val accountId = request.getParameter("accountId")
            val list = gateway.findBy(accountId.toLong()).map { record ->
                ProjectInfoV1(record.id, record.accountId, record.name, record.active, "project info")
            }
            mapper.writeValue(httpServletResponse.outputStream, list)
        }
        get("/project", asList("application/json", "application/vnd.appcontinuum.v1+json"), request, httpServletResponse) {
            val projectId = request.getParameter("projectId")
            val record = gateway.findObject(projectId.toLong())
            if (record != null) {
                val project = ProjectInfoV1(record.id, record.accountId, record.name, record.active, "project info")
                mapper.writeValue(httpServletResponse.outputStream, project)
            }
        }
    }
}