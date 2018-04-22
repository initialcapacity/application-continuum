package test.barinek.continuum.backlog

import io.barinek.continuum.backlog.StoryDataGateway
import io.barinek.continuum.jdbcsupport.DataSourceConfig
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class StoryDataGatewayTest() {
    val dataSource = DataSourceConfig().createDataSource("backlog")
    val template = JdbcTemplate(dataSource)
    val gateway = StoryDataGateway(template)

    @Before
    fun cleanDatabase() {
        JdbcTemplate(dataSource).apply {
            execute("delete from stories")
        }
    }

    @Test
    fun testCreate() {
        gateway.create(22L, "aStory")

        val foundStory = template.query("select id, project_id, name from stories", { },
                { rs -> listOf(rs.getLong("id"), rs.getLong("project_id"), rs.getString("name")) }).first()

        assert(foundStory[0] as Long > 0)
        assertEquals(22L, foundStory[1])
        assertEquals("aStory", foundStory[2])
    }

    @Test
    fun testFindBy() {
        template.execute("insert into stories (id, project_id, name) values (1346, 22, 'aStory')")

        val story = gateway.findBy(22L)[0]

        assertEquals(1346L, story.id)
        assertEquals(22L, story.projectId)
        assertEquals("aStory", story.name)
    }
}