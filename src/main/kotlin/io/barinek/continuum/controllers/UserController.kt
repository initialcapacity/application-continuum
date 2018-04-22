package io.barinek.continuum.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.barinek.continuum.utils.BasicHandler
import io.barinek.continuum.dataaccess.UserDataGateway
import io.barinek.continuum.models.UserInfo
import org.eclipse.jetty.server.Request
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class UserController(val mapper: ObjectMapper, val gateway: UserDataGateway) : BasicHandler() {

    override fun handle(s: String, request: Request, httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) {
        get("/users", request, httpServletResponse) {
            val userId = request.getParameter("userId")
            val record = gateway.findObjectBy(userId.toLong())
            mapper.writeValue(httpServletResponse.outputStream, UserInfo(record.id, record.name, "user info"))
        }
    }
}