package test.barinek.continuum.accounts

import io.barinek.continuum.accounts.AccountDataGateway
import io.barinek.continuum.accounts.RegistrationController
import io.barinek.continuum.accounts.RegistrationService
import io.barinek.continuum.jdbcsupport.DataSourceConfig
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.jdbcsupport.TransactionManager
import io.barinek.continuum.restsupport.BasicApp
import io.barinek.continuum.testsupport.TestControllerSupport
import io.barinek.continuum.testsupport.TestScenarioSupport
import io.barinek.continuum.users.UserDataGateway
import io.barinek.continuum.users.UserInfo
import org.eclipse.jetty.server.handler.HandlerList
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class RegistrationControllerTest : TestControllerSupport() {
    val dataSource = DataSourceConfig().createDataSource("registration")

    private var app: BasicApp = object : BasicApp() {
        override fun getPort() = 8081

        override fun handlerList() = HandlerList().apply {
            val transactionManager = TransactionManager(dataSource)
            val template = JdbcTemplate(dataSource)
            addHandler(RegistrationController(mapper, RegistrationService(transactionManager, UserDataGateway(template), AccountDataGateway(template))))
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
    fun testRegister() {
        TestScenarioSupport(dataSource).loadTestScenario("jacks-test-scenario")

        val registrationResponse = template.post("http://localhost:8081/registration", "{\"name\":\"aUser\"}")
        val actual = mapper.readValue(registrationResponse, UserInfo::class.java)

        assert(actual.id > 0)
        assertEquals("aUser", actual.name)
        assertEquals("registration info", actual.info)
    }
}