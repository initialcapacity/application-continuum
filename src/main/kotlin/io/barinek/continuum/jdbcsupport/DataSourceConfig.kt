package io.barinek.continuum.jdbcsupport

import com.fasterxml.jackson.databind.ObjectMapper
import com.zaxxer.hikari.HikariDataSource

open class DataSourceConfig {
    fun createDataSource(name: String = "appcontinuum"): HikariDataSource {
        val json = System.getenv("VCAP_SERVICES")
        val dataSource = HikariDataSource()
        dataSource.jdbcUrl = from(name, json)
        return dataSource
    }

    fun from(name: String, json: String): String? {
        val mapper = ObjectMapper()
        val root = mapper.readTree(json)
        root.findValue("p-mysql").forEach { services ->
            val node = services.findValue("name")
            if (node.textValue() == name) {
                val credentials = services.findValue("credentials")
                return credentials.findValue("jdbcUrl").textValue()
            }
        }
        return null
    }
}