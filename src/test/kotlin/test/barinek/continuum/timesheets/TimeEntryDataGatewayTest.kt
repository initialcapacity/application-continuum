package test.barinek.continuum.timesheets

import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.timesheets.TimeEntryDataGateway
import org.junit.Test
import test.barinek.continuum.jdbcsupport.TestDataSourceConfig
import java.time.LocalDate
import kotlin.test.assertEquals

class TimeEntryDataGatewayTest() {
    val dataSource = TestDataSourceConfig().dataSource
    val template = JdbcTemplate(dataSource)
    val gateway = TimeEntryDataGateway(template)
    val now: LocalDate = LocalDate.now()

    @Test
    fun testCreate() {
        template.execute("insert into users (id, name) values (12, 'Jack')")
        template.execute("insert into accounts (id, owner_id, name) values (1, 12, 'anAccount')")
        template.execute("insert into projects (id, account_id, name) values (22, 1, 'aProject')")

        gateway.create(22L, 12L, now, 8)

        val actual = template.query("select id, project_id, user_id, date, hours from time_entries", { },
                { rs -> listOf(rs.getLong("id"), rs.getLong("project_id"), rs.getLong("user_id"), rs.getDate("date").toLocalDate(), rs.getInt("hours")) }).first()

        assert(actual[0] as Long > 0)
        assertEquals(22L, actual[1])
        assertEquals(12L, actual[2])
        assertEquals(now, actual[3])
        assertEquals(8, actual[4])
    }

    @Test
    fun testFindBy() {
        template.execute("insert into users (id, name) values (12, 'Jack')")
        template.execute("insert into accounts (id, owner_id, name) values (1, 12, 'anAccount')")
        template.execute("insert into projects (id, account_id, name) values (22, 1, 'aProject')")
        template.execute("insert into time_entries (id, project_id, user_id, date, hours) values (2346, 22, 12, '$now', 8)")

        val actual = gateway.findBy(12L).first()

        assertEquals(2346L, actual.id)
        assertEquals(22L, actual.projectId)
        assertEquals(12L, actual.userId)
        assertEquals(now, actual.date)
        assertEquals(8, actual.hours)
    }
}