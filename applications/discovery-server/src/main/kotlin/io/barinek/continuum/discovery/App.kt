package io.barinek.continuum.discovery

import io.barinek.continuum.redissupport.RedisConfig
import io.barinek.continuum.restsupport.BasicApp
import io.barinek.continuum.restsupport.DefaultController
import org.eclipse.jetty.server.handler.HandlerList
import java.util.*

class App : BasicApp() {
    override fun getPort() = System.getenv("PORT").toInt()

    override fun handlerList(): HandlerList {
        val pool = RedisConfig().getPool("discovery")

        return HandlerList().apply {  // ordered
            addHandler(DiscoveryController(mapper, InstanceDataGateway(pool, 60000L)))
            addHandler(DefaultController())
        }
    }
}

fun main(args: Array<String>) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    App().start()
}