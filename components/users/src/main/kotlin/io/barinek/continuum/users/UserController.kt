package io.barinek.continuum.users

import com.fasterxml.jackson.databind.ObjectMapper
import io.barinek.continuum.restsupport.BasicHandler
import org.eclipse.jetty.server.Request
import java.util.Arrays.asList
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class UserController(val mapper: ObjectMapper, val gateway: UserDataGateway) : BasicHandler() {

    override fun handle(s: String, request: Request, httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) {
        get("/users", asList("application/json", "application/vnd.appcontinuum.v1+json"), request, httpServletResponse) {
            val userId = request.getParameter("userId")
            val record = gateway.findObjectBy(userId.toLong())
            mapper.writeValue(httpServletResponse.outputStream, UserInfo(record.id, record.name, "user info"))
        }
    }
}