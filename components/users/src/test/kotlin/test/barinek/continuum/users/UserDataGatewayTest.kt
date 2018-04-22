package test.barinek.continuum.users

import io.barinek.continuum.jdbcsupport.DataSourceConfig
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.users.UserDataGateway
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class UserDataGatewayTest() {
    val dataSource = DataSourceConfig().createDataSource("registration")
    val template = JdbcTemplate(dataSource)
    val gateway = UserDataGateway(template)

    @Before
    fun cleanDatabase() {
        JdbcTemplate(dataSource).apply {
            execute("delete from projects")
            execute("delete from accounts")
            execute("delete from users")
        }
    }

    @Test
    fun testCreate() {
        gateway.create("aUser")

        val actual = template.query("select id, name from users", { },
                { rs -> listOf(rs.getLong("id"), rs.getString("name")) }).first()

        assert(actual[0] as Long > 0)
        assertEquals("aUser", actual[1])
    }

    @Test
    fun testFindBy() {
        template.execute("""
            insert into users (id, name) values
               (42346, 'aName')
             , (42347, 'anotherName')
             , (42348, 'andAnotherName')""")

        val actual = gateway.findObjectBy(42347L)

        assertEquals(42347, actual.id)
        assertEquals("anotherName", actual.name)
    }
}