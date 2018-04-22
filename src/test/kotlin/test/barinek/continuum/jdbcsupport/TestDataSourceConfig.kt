package test.barinek.continuum.jdbcsupport

import io.barinek.continuum.jdbcsupport.DataSourceConfig
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import javax.sql.DataSource

class TestDataSourceConfig(val dataSource: DataSource = hikariDataSource) {
    companion object {
        val hikariDataSource = DataSourceConfig().createDataSource() // one data source for testing
    }

    init {
        JdbcTemplate(hikariDataSource).apply {
            execute("delete from allocations")
            execute("delete from stories")
            execute("delete from time_entries")
            execute("delete from projects")
            execute("delete from accounts")
            execute("delete from users")
        }
    }
}