package test.barinek.continuum.services

import io.barinek.continuum.accounts.AccountDataGateway
import io.barinek.continuum.users.UserDataGateway
import io.barinek.continuum.accounts.RegistrationService
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.jdbcsupport.TransactionManager
import org.junit.Test
import test.barinek.continuum.jdbcsupport.TestDataSourceConfig
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