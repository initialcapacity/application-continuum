package io.barinek.continuum.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.barinek.continuum.utils.BasicHandler
import io.barinek.continuum.services.RegistrationService
import io.barinek.continuum.models.UserInfo
import org.eclipse.jetty.server.Request
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RegistrationController(val mapper: ObjectMapper, val service: RegistrationService) : BasicHandler() {

    override fun handle(s: String, request: Request, httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) {
        post("/registration", request, httpServletResponse) {
            val user = mapper.readValue(request.reader, UserInfo::class.java)
            val record = service.createUserWithAccount(user.name)
            mapper.writeValue(httpServletResponse.outputStream, UserInfo(record.id, record.name, "registration info"))
        }
    }
}