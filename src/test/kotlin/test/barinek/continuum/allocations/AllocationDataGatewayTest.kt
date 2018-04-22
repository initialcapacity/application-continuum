package test.barinek.continuum.allocations

import io.barinek.continuum.allocations.AllocationDataGateway
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import org.junit.Test
import test.barinek.continuum.jdbcsupport.TestDataSourceConfig
import java.time.LocalDate
import kotlin.test.assertEquals

class AllocationDataGatewayTest() {
    val dataSource = TestDataSourceConfig().dataSource
    val template = JdbcTemplate(dataSource)
    val gateway = AllocationDataGateway(template)
    val now: LocalDate = LocalDate.now()

    @Test
    fun testCreate() {
        template.execute("insert into users (id, name) values (12, 'Jack')")
        template.execute("insert into accounts (id, owner_id, name) values (1, 12, 'anAccount')")
        template.execute("insert into projects (id, account_id, name) values (22, 1, 'aProject')")

        gateway.create(22L, 12L, now, now)

        val actual = template.query("select id, project_id, user_id, first_day, last_day from allocations", { },
                { rs -> listOf(rs.getLong("id"), rs.getLong("project_id"), rs.getLong("user_id"), rs.getDate("first_day").toLocalDate(), rs.getDate("last_day").toLocalDate()) }).first()

        assert(actual[0] as Long > 0)
        assertEquals(22L, actual[1])
        assertEquals(12L, actual[2])
        assertEquals(now, actual[3])
        assertEquals(now, actual[4])
    }

    @Test
    fun testFindBy() {
        template.execute("insert into users (id, name) values (12, 'Jack')")
        template.execute("insert into accounts (id, owner_id, name) values (1, 12, 'anAccount')")
        template.execute("insert into projects (id, account_id, name) values (22, 1, 'aProject')")
        template.execute("insert into allocations (id, project_id, user_id, first_day, last_day) values (97336, 22, 12, '$now', '$now')")

        val actual = gateway.findBy(22L).first()

        assertEquals(97336, actual.id)
        assertEquals(22L, actual.projectId)
        assertEquals(12L, actual.userId)
        assertEquals(now, actual.firstDay)
        assertEquals(now, actual.lastDay)
    }
}