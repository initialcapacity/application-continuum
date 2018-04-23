package io.barinek.continuum.restsupport

import org.eclipse.jetty.http.HttpMethod
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import java.sql.SQLException
import javax.servlet.http.HttpServletResponse

abstract class BasicHandler : AbstractHandler() {
    fun post(uri: String, supportedMediaTypes: List<String>, request: Request, httpServletResponse: HttpServletResponse, block: () -> Unit) {
        if (request.method == HttpMethod.POST.toString()) {
            if (uri == request.requestURI) {
                val acceptedMediaType = request.getHeader("Accept")

                for (supportedMediaType in supportedMediaTypes) {
                    if (supportedMediaType == acceptedMediaType) {
                        httpServletResponse.contentType = supportedMediaType
                        try {
                            httpServletResponse.status = HttpServletResponse.SC_CREATED
                            block()
                        } catch (e: SQLException) {
                            httpServletResponse.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                        }
                        request.isHandled = true
                    }
                }
            }
        }
    }

    fun get(uri: String, supportedMediaTypes: List<String>, request: Request, httpServletResponse: HttpServletResponse, block: () -> Unit) {
        if (request.method == HttpMethod.GET.toString()) {
            if (uri == request.requestURI) {
                val acceptedMediaType = request.getHeader("Accept")

                for (supportedMediaType in supportedMediaTypes) {
                    if (supportedMediaType == acceptedMediaType) {
                        httpServletResponse.contentType = supportedMediaType
                        try {
                            httpServletResponse.status = HttpServletResponse.SC_OK
                            block()
                        } catch (e: SQLException) {
                            httpServletResponse.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                        }
                        request.isHandled = true
                    }
                }
            }
        }
    }
}