package io.barinek.continuum.services

import io.barinek.continuum.dataaccess.AccountDataGateway
import io.barinek.continuum.dataaccess.UserDataGateway
import io.barinek.continuum.models.UserRecord
import io.barinek.continuum.utils.TransactionManager

class RegistrationService(val transactionManager: TransactionManager, val userDataGateway: UserDataGateway, val accountDataGateway: AccountDataGateway) {

    fun createUserWithAccount(name: String): UserRecord {
        return transactionManager.withTransaction { connection ->
            val user = userDataGateway.create(connection, name)
            accountDataGateway.create(connection, user.id, String.format("%s's account", name))
            user
        }
    }
}