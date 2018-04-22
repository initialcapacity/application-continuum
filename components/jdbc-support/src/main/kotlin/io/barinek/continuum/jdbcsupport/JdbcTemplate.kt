package io.barinek.continuum.jdbcsupport

import kotlinx.support.jdk7.use
import java.sql.*
import java.sql.Date
import java.time.LocalDate
import java.util.*
import javax.sql.DataSource

class JdbcTemplate(val dataSource: DataSource) {

    fun <T> create(sql: String, id: (Long) -> T, vararg params: Any) =
            dataSource.connection.use { connection ->
                create(connection, sql, id, *params)
            }

    fun <T> create(connection: Connection, sql: String, id: (Long) -> T, vararg params: Any): T {
        return connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { statement ->
            for (i in params.indices) {
                val param = params[i]
                val parameterIndex = i + 1

                when (param) {
                    is String -> statement.setString(parameterIndex, param)
                    is Int -> statement.setInt(parameterIndex, param)
                    is Long -> statement.setLong(parameterIndex, param)
                    is Boolean -> statement.setBoolean(parameterIndex, param)
                    is LocalDate -> statement.setDate(parameterIndex, Date.valueOf(param))

                }
            }
            statement.executeUpdate()
            val keys = statement.generatedKeys
            keys.next()
            id(keys.getLong(1))
        }
    }

    fun <T> findObject(sql: String, mapper: (ResultSet) -> T, id: Long): T? {
        val list = query(sql, { ps -> ps.setLong(1, id) }, mapper)
        when {
            list.isEmpty() -> return null

            else -> return list.first()
        }
    }

    fun <T> findBy(sql: String, mapper: (ResultSet) -> T, id: Long) = query(sql, { ps -> ps.setLong(1, id) }, mapper)

    /// USED FOR TESTING

    fun execute(sql: String) {
        dataSource.connection.use { connection ->
            connection.prepareCall(sql).use(CallableStatement::execute)
        }
    }

    fun <T> query(sql: String, params: (PreparedStatement) -> Unit, mapper: (ResultSet) -> T): List<T> {
        val results = ArrayList<T>()

        dataSource.connection.use { connection ->
            connection.prepareStatement(sql).use { statement ->
                params(statement)
                statement.executeQuery().use { rs ->
                    while (rs.next()) {
                        results.add(mapper(rs))
                    }
                }
            }
        }
        return results
    }
}