package io.barinek.continuum.allocations

import io.barinek.continuum.jdbcsupport.DataSourceConfig
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.restsupport.BasicApp
import io.barinek.continuum.restsupport.DefaultController
import io.barinek.continuum.restsupport.RestTemplate
import org.eclipse.jetty.server.handler.HandlerList
import java.util.*

class App : BasicApp() {
    override fun getPort() = System.getenv("PORT").toInt()

    override fun handlerList(): HandlerList {
        val dataSource = DataSourceConfig().createDataSource("allocations")
        val template = JdbcTemplate(dataSource)

        return HandlerList().apply { // ordered
            addHandler(AllocationController(mapper, AllocationDataGateway(template), ProjectClient(mapper, RestTemplate())))
            addHandler(DefaultController())
        }
    }
}

fun main(args: Array<String>) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    App().start()
}