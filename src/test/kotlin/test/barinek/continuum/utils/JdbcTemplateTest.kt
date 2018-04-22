package test.barinek.continuum.utils

import io.barinek.continuum.utils.JdbcTemplate
import org.junit.Test
import kotlin.test.assertEquals

class JdbcTemplateTest() {
    val dataSource = TestDataSourceConfig().dataSource
    val template = JdbcTemplate(dataSource)

    @Test
    fun testFind() {
        val id = 42
        val sql = "select id, name from (select 42 as id, 'apples' as name) as dates where id = ?"

        val names = template.query(sql, { ps -> ps.setInt(1, id) }, { rs -> rs.getString(2) })
        assertEquals("apples", names[0])
    }
}