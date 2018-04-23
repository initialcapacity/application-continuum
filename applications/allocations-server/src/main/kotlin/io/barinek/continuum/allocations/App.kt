package io.barinek.continuum.allocations

import io.barinek.continuum.discovery.DiscoveryClient
import io.barinek.continuum.jdbcsupport.DataSourceConfig
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.restsupport.BasicApp
import io.barinek.continuum.restsupport.DefaultController
import io.barinek.continuum.restsupport.RestTemplate
import org.eclipse.jetty.server.handler.HandlerList
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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

    override fun start() {
        super.start()
        Executors.newSingleThreadScheduledExecutor(Executors.defaultThreadFactory()).scheduleAtFixedRate({
            DiscoveryClient(mapper, RestTemplate()).heartbeat("allocation", server.uri.toString())
        }, 0L, 30L, TimeUnit.SECONDS)
    }
}

fun main(args: Array<String>) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    App().start()
}