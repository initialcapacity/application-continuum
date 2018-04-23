package test.barinek.continuum.users

import io.barinek.continuum.jdbcsupport.DataSourceConfig
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.restsupport.BasicApp
import io.barinek.continuum.testsupport.TestControllerSupport
import io.barinek.continuum.testsupport.TestScenarioSupport
import io.barinek.continuum.users.UserController
import io.barinek.continuum.users.UserDataGateway
import io.barinek.continuum.users.UserInfo
import org.apache.http.message.BasicNameValuePair
import org.eclipse.jetty.server.handler.HandlerList
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class UserControllerTest : TestControllerSupport() {
    val dataSource = DataSourceConfig().createDataSource("registration")

    private var app: BasicApp = object : BasicApp() {
        override fun getPort() = 8081

        override fun handlerList() = HandlerList().apply {
            addHandler(UserController(mapper, UserDataGateway(JdbcTemplate(dataSource))))
        }
    }

    @Before
    fun setUp() {
        JdbcTemplate(dataSource).apply {
            execute("delete from projects")
            execute("delete from accounts")
            execute("delete from users")
        }
        app.start()
    }

    @After
    fun tearDown() {
        app.stop()
    }

    @Test
    fun testShow() {
        TestScenarioSupport(dataSource).loadTestScenario("jacks-test-scenario")

        val response = template.get("http://localhost:8081/users", "application/json", BasicNameValuePair("userId", "4765"))
        val actual = mapper.readValue(response, UserInfo::class.java)

        assertEquals(4765L, actual.id)
        assertEquals("Jack", actual.name)
        assertEquals("user info", actual.info)
    }
}