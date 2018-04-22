package test.barinek.continuum

import io.barinek.continuum.JdbcTemplate
import io.barinek.continuum.StoryDataGateway
import org.junit.Test
import kotlin.test.assertEquals

class StoryDataGatewayTest() {
    val dataSource = TestDataSourceConfig().dataSource
    val template = JdbcTemplate(dataSource)
    val gateway = StoryDataGateway(template)

    @Test
    fun testCreate() {
        template.execute("insert into users (id, name) values (12, 'Jack')")
        template.execute("insert into accounts (id, owner_id, name) values (1, 12, 'anAccount')")
        template.execute("insert into projects (id, account_id, name) values (22, 1, 'aProject')")

        gateway.create(22L, "aStory")

        val foundStory = template.query("select id, project_id, name from stories", { },
                { rs -> listOf(rs.getLong("id"), rs.getLong("project_id"), rs.getString("name")) }).first()

        assert(foundStory[0] as Long > 0)
        assertEquals(22L, foundStory[1])
        assertEquals("aStory", foundStory[2])
    }

    @Test
    fun testFindBy() {
        template.execute("insert into users (id, name) values (12, 'Jack')")
        template.execute("insert into accounts (id, owner_id, name) values (1, 12, 'anAccount')")
        template.execute("insert into projects (id, account_id, name) values (22, 1, 'aProject')")
        template.execute("insert into stories (id, project_id, name) values (1346, 22, 'aStory')")

        val story = gateway.findBy(22L)[0]

        assertEquals(1346L, story.id)
        assertEquals(22L, story.projectId)
        assertEquals("aStory", story.name)
    }
}