package test.barinek.continuum.restsupport

import io.barinek.continuum.restsupport.BasicApp
import io.barinek.continuum.restsupport.DefaultController
import io.barinek.continuum.restsupport.RestTemplate
import org.eclipse.jetty.server.handler.HandlerList
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class DefaultControllerTest {
    val template = RestTemplate()

    private var app: BasicApp = object : BasicApp() {
        override fun getPort() = 8081

        override fun handlerList() = HandlerList().apply {
            addHandler(DefaultController())
        }
    }

    @Before
    fun setUp() {
        app.start()
    }

    @After
    fun tearDown() {
        app.stop()
    }

    @Test
    fun testGet() {
        val response = template.get("http://localhost:8081/")
        assertEquals("Noop!", response)
    }
}