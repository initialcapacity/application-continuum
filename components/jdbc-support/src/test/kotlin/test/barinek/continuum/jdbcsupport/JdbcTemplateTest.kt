package test.barinek.continuum.jdbcsupport

import io.barinek.continuum.jdbcsupport.DataSourceConfig
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class JdbcTemplateTest() {
    val dataSource = DataSourceConfig().createDataSource("registration")
    val template = JdbcTemplate(dataSource)

    @Test
    fun testFind() {
        val id = 42
        val sql = "select id, name from (select 42 as id, 'apples' as name) as dates where id = ?"

        val names = template.query(sql, { ps -> ps.setInt(1, id) }, { rs -> rs.getString(2) })
        assertEquals("apples", names[0])
    }

    @Test
    fun testFindObject() {
        val sql = "select id, name from (select 42 as id, 'apples' as name) as dates where id = ?"

        var actual = template.findObject(sql, { ps -> ps.getInt(1) }, 42)
        assertEquals(42, actual)

        actual = template.findObject(sql, { ps -> ps.getInt(1) }, 44)
        assertNull(actual)
    }
}