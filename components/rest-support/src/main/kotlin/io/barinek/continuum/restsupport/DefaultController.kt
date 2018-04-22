package io.barinek.continuum.restsupport

import org.eclipse.jetty.server.Request
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class DefaultController : BasicHandler() {
    override fun handle(s: String, request: Request, httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse) {
        httpServletResponse.contentType = "text/html; charset=UTF-8"
        httpServletResponse.outputStream.write("Noop!".toByteArray())
        httpServletResponse.status = HttpServletResponse.SC_OK
        request.isHandled = true
    }
}