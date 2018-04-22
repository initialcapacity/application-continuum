package test.barinek.continuum.accounts

import com.fasterxml.jackson.core.type.TypeReference
import io.barinek.continuum.accounts.AccountController
import io.barinek.continuum.accounts.AccountDataGateway
import io.barinek.continuum.accounts.AccountInfo
import io.barinek.continuum.jdbcsupport.DataSourceConfig
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.restsupport.BasicApp
import io.barinek.continuum.testsupport.TestControllerSupport
import io.barinek.continuum.testsupport.TestScenarioSupport
import org.apache.http.message.BasicNameValuePair
import org.eclipse.jetty.server.handler.HandlerList
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class AccountControllerTest : TestControllerSupport() {
    val dataSource = DataSourceConfig().createDataSource("registration")

    private var app: BasicApp = object : BasicApp() {
        override fun getPort() = 8081

        override fun handlerList() = HandlerList().apply {
            addHandler(AccountController(mapper, AccountDataGateway(JdbcTemplate(dataSource))))
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
    fun testFind() {
        TestScenarioSupport(dataSource).loadTestScenario("jacks-test-scenario")

        val ownerId = BasicNameValuePair("ownerId", "4765")
        val response = template.get("http://localhost:8081/accounts", ownerId)
        val list: List<AccountInfo> = mapper.readValue(response, object : TypeReference<List<AccountInfo>>() {})
        val actual = list.first()

        assertEquals(1673L, actual.id)
        assertEquals(4765L, actual.ownerId)
        assertEquals("Jack's account", actual.name)
        assertEquals("account info", actual.info)
    }
}