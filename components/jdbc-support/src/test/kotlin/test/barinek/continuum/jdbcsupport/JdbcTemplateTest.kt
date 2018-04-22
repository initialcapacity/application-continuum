package test.barinek.continuum.jdbcsupport

import io.barinek.continuum.jdbcsupport.DataSourceConfig
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import org.junit.Test
import kotlin.test.assertEquals

class JdbcTemplateTest() {
    val dataSource = DataSourceConfig().createDataSource()
    val template = JdbcTemplate(dataSource)

    @Test
    fun testFind() {
        val id = 42
        val sql = "select id, name from (select 42 as id, 'apples' as name) as dates where id = ?"

        val names = template.query(sql, { ps -> ps.setInt(1, id) }, { rs -> rs.getString(2) })
        assertEquals("apples", names[0])
    }
}