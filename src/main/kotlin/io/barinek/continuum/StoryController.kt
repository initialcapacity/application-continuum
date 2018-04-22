package io.barinek.continuum

import com.fasterxml.jackson.databind.ObjectMapper
import org.eclipse.jetty.server.Request
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class StoryController(val mapper: ObjectMapper, val gateway: StoryDataGateway) : BasicHandler() {

    override fun handle(s: String, request: Request, httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) {
        post("/stories", request, httpServletResponse) {
            val story = mapper.readValue(request.reader, StoryInfo::class.java)
            val record = gateway.create(story.projectId, story.name)
            mapper.writeValue(httpServletResponse.outputStream, StoryInfo(record.id, record.projectId, record.name, "story info"))
        }
        get("/stories", request, httpServletResponse) {
            val projectId = request.getParameter("projectId")
            val list = gateway.findBy(projectId.toLong()).map { record ->
                StoryInfo(record.id, record.projectId, record.name, "story info")
            }
            mapper.writeValue(httpServletResponse.outputStream, list)
        }
    }

}