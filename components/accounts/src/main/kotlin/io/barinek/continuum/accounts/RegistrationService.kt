package io.barinek.continuum.accounts

import io.barinek.continuum.jdbcsupport.TransactionManager
import io.barinek.continuum.users.UserDataGateway
import io.barinek.continuum.users.UserRecord

class RegistrationService(val transactionManager: TransactionManager, val userDataGateway: UserDataGateway, val accountDataGateway: AccountDataGateway) {

    fun createUserWithAccount(name: String): UserRecord {
        return transactionManager.withTransaction { connection ->
            val user = userDataGateway.create(connection, name)
            accountDataGateway.create(connection, user.id, String.format("%s's account", name))
            user
        }
    }
}