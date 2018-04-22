package test.barinek.continuum.accounts

import io.barinek.continuum.accounts.AccountDataGateway
import io.barinek.continuum.accounts.RegistrationService
import io.barinek.continuum.jdbcsupport.DataSourceConfig
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.jdbcsupport.TransactionManager
import io.barinek.continuum.users.UserDataGateway
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class RegistrationServiceTest() {
    val dataSource = DataSourceConfig().createDataSource("registration")

    @Before
    fun cleanDatabase() {
        JdbcTemplate(dataSource).apply {
            execute("delete from projects")
            execute("delete from accounts")
            execute("delete from users")
        }
    }

    @Test
    fun testCreateUserWithAccount() {
        val template = JdbcTemplate(dataSource)
        val transactionManager = TransactionManager(dataSource)

        val usersGateway = UserDataGateway(template)
        val accountsGateway = AccountDataGateway(template)

        val service = RegistrationService(transactionManager, usersGateway, accountsGateway)
        val aUser = service.createUserWithAccount("aUser")

        assertEquals("aUser", aUser.name)
    }
}