package io.barinek.continuum.restsupport

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.barinek.continuum.jdbcsupport.DataSourceConfig
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.jdbcsupport.TransactionManager
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.HandlerList
import org.slf4j.LoggerFactory

abstract class BasicApp {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val server: Server
    private val dataSource = DataSourceConfig().createDataSource()
    private val transactionManager = TransactionManager(dataSource)
    private val template = JdbcTemplate(dataSource)

    val mapper: ObjectMapper = ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())

    init {
        val list = handlerList(transactionManager, template)

        server = Server(getPort())
        server.handler = list
        server.stopAtShutdown = true;

        Runtime.getRuntime().addShutdownHook(Thread({
            try {
                if (server.isRunning) {
                    server.stop()
                }
                logger.info("App shutdown.")
            } catch (e: Exception) {
                logger.info("Error shutting down app.", e)
            }
        }))
    }

    protected abstract fun getPort(): Int

    protected abstract fun handlerList(transactionManager: TransactionManager, template: JdbcTemplate): HandlerList

    fun start() {
        logger.info("App started.")
        server.start()
    }

    fun stop() {
        logger.info("App stopped.")
        server.stop()
    }
}