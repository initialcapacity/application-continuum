package test.barinek.continuum

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.barinek.continuum.App
import io.barinek.continuum.restsupport.RestTemplate
import org.junit.After
import org.junit.Before

open class TestControllerSupport {
    val mapper = ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())!!
    val template = RestTemplate()
    val app = App()

    @Before
    fun setUp() {
        app.start()
    }

    @After
    fun tearDown() {
        app.stop()
    }
}


