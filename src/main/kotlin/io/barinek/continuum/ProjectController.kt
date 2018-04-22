package io.barinek.continuum

import com.fasterxml.jackson.databind.ObjectMapper
import org.eclipse.jetty.server.Request
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ProjectController(val mapper: ObjectMapper, val gateway: ProjectDataGateway) : BasicHandler() {

    override fun handle(s: String, request: Request, httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) {
        post("/projects", request, httpServletResponse) {
            val project = mapper.readValue(request.reader, ProjectInfo::class.java)
            val record = gateway.create(project.accountId, project.name)
            mapper.writeValue(httpServletResponse.outputStream, ProjectInfo(record.id, record.accountId, record.name, "project info"))
        }
        get("/projects", request, httpServletResponse) {
            val accountId = request.getParameter("accountId")
            val list = gateway.findBy(accountId.toLong()).map { record ->
                ProjectInfo(record.id, record.accountId, record.name, "project info")
            }
            mapper.writeValue(httpServletResponse.outputStream, list)
        }
    }
}