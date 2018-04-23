package io.barinek.continuum.projects

import com.fasterxml.jackson.databind.ObjectMapper
import io.barinek.continuum.restsupport.BasicHandler
import org.eclipse.jetty.server.Request
import java.util.Arrays.asList
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ProjectControllerV2(val mapper: ObjectMapper, val gateway: ProjectDataGateway) : BasicHandler() {

    override fun handle(s: String, request: Request, httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) {
        post("/projects", asList("application/vnd.appcontinuum.v2+json"), request, httpServletResponse) {
            val project = mapper.readValue(request.reader, ProjectInfoV2::class.java)
            val record = gateway.create(project.accountId, project.name, project.active, project.funded)
            mapper.writeValue(httpServletResponse.outputStream, ProjectInfoV2(record.id, record.accountId, record.name, record.active, record.funded, "project info"))
        }
        get("/projects", asList("application/vnd.appcontinuum.v2+json"), request, httpServletResponse) {
            val accountId = request.getParameter("accountId")
            val list = gateway.findBy(accountId.toLong()).map { record ->
                ProjectInfoV2(record.id, record.accountId, record.name, record.active, record.funded, "project info")
            }
            mapper.writeValue(httpServletResponse.outputStream, list)
        }
        get("/project", asList("application/vnd.appcontinuum.v2+json"), request, httpServletResponse) {
            val projectId = request.getParameter("projectId")
            val record = gateway.findObject(projectId.toLong())
            if (record != null) {
                val project = ProjectInfoV2(record.id, record.accountId, record.name, record.active, record.funded, "project info")
                mapper.writeValue(httpServletResponse.outputStream, project)
            }
        }
    }
}