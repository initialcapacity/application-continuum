package io.barinek.continuum.backlog

import com.fasterxml.jackson.databind.ObjectMapper
import io.barinek.continuum.restsupport.BasicHandler
import org.eclipse.jetty.server.Request
import java.util.Arrays.asList
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class StoryController(val mapper: ObjectMapper, val gateway: StoryDataGateway, val client: ProjectClient) : BasicHandler() {

    override fun handle(s: String, request: Request, httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) {
        post("/stories", asList("application/json", "application/vnd.appcontinuum.v1+json"), request, httpServletResponse) {
            val story = mapper.readValue(request.reader, StoryInfo::class.java)

            if (projectIsActive(story.projectId)) {
                val record = gateway.create(story.projectId, story.name)
                mapper.writeValue(httpServletResponse.outputStream, StoryInfo(record.id, record.projectId, record.name, "story info"))
            }
        }
        get("/stories", asList("application/json", "application/vnd.appcontinuum.v1+json"), request, httpServletResponse) {
            val projectId = request.getParameter("projectId")
            val list = gateway.findBy(projectId.toLong()).map { record ->
                StoryInfo(record.id, record.projectId, record.name, "story info")
            }
            mapper.writeValue(httpServletResponse.outputStream, list)
        }
    }

    private fun projectIsActive(projectId: Long): Boolean {
        val project = client.getProject(projectId)
        return project != null && project.active
    }
}