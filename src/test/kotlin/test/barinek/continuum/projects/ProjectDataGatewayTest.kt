package test.barinek.continuum.projects

import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.projects.ProjectDataGateway
import org.junit.Test
import test.barinek.continuum.jdbcsupport.TestDataSourceConfig
import kotlin.test.assertEquals

class ProjectDataGatewayTest() {
    val dataSource = TestDataSourceConfig().dataSource
    val template = JdbcTemplate(dataSource)
    val gateway = ProjectDataGateway(template)

    @Test
    fun testCreate() {
        template.execute("insert into users (id, name) values (12, 'Jack')")
        template.execute("insert into accounts (id, owner_id, name) values (1, 12, 'anAccount')")

        gateway.create(1L, "aProject")

        val actual = template.query("select id, account_id, name from projects", { },
                { rs -> listOf(rs.getLong("id"), rs.getLong("account_id"), rs.getString("name")) }).first()

        assert(actual[0] as Long > 0)
        assertEquals(1L, actual[1])
        assertEquals("aProject", actual[2])
    }

    @Test
    fun testFindBy() {
        template.execute("insert into users (id, name) values (12, 'Jack')")
        template.execute("insert into accounts (id, owner_id, name) values (1, 12, 'anAccount')")
        template.execute("insert into projects (id, account_id, name) values (22, 1, 'aProject')")

        val actual = gateway.findBy(1L).first()

        assertEquals(22L, actual.id)
        assertEquals(1L, actual.accountId)
        assertEquals("aProject", actual.name)
    }
}