package test.barinek.continuum

import io.barinek.continuum.DataSourceConfig
import io.barinek.continuum.JdbcTemplate
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