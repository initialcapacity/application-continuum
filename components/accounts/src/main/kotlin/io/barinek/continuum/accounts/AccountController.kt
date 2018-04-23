package io.barinek.continuum.accounts

import com.fasterxml.jackson.databind.ObjectMapper
import io.barinek.continuum.restsupport.BasicHandler
import org.eclipse.jetty.server.Request
import java.util.Arrays.asList
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AccountController(val mapper: ObjectMapper, val gateway: AccountDataGateway) : BasicHandler() {

    override fun handle(s: String, request: Request, httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) {
        get("/accounts", asList("application/json", "application/vnd.appcontinuum.v1+json"), request, httpServletResponse) {
            val ownerId = request.getParameter("ownerId")
            val list = gateway.findBy(ownerId.toLong()).map { record ->
                AccountInfo(record.id, record.ownerId, record.name, "account info")
            }
            mapper.writeValue(httpServletResponse.outputStream, list)
        }
    }
}