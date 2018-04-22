package test.barinek.continuum

import io.barinek.continuum.*
import org.junit.Test
import kotlin.test.assertEquals

class RegistrationServiceTest() {
    @Test
    fun testCreateUserWithAccount() {
        val dataSource = TestDataSourceConfig().dataSource
        val template = JdbcTemplate(dataSource)
        val transactionManager = TransactionManager(dataSource)

        val usersGateway = UserDataGateway(template)
        val accountsGateway = AccountDataGateway(template)

        val service = RegistrationService(transactionManager, usersGateway, accountsGateway)
        val aUser = service.createUserWithAccount("aUser")

        assertEquals("aUser", aUser.name)
    }
}