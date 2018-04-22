package io.barinek.continuum

import kotlinx.support.jdk7.use
import java.sql.Connection
import javax.sql.DataSource

class TransactionManager(val dataSource: DataSource) {

    fun <T> withTransaction(function: (Connection) -> T): T {
        dataSource.connection.use { connection ->
            connection.autoCommit = false
            val results = function(connection)
            connection.commit()
            connection.autoCommit = true
            return results
        }
    }
}