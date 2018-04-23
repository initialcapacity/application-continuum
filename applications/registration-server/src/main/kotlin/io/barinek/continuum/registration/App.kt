package io.barinek.continuum.registration

import io.barinek.continuum.accounts.AccountController
import io.barinek.continuum.accounts.AccountDataGateway
import io.barinek.continuum.accounts.RegistrationController
import io.barinek.continuum.accounts.RegistrationService
import io.barinek.continuum.jdbcsupport.DataSourceConfig
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.jdbcsupport.TransactionManager
import io.barinek.continuum.projects.ProjectControllerV1
import io.barinek.continuum.projects.ProjectControllerV2
import io.barinek.continuum.projects.ProjectDataGateway
import io.barinek.continuum.restsupport.BasicApp
import io.barinek.continuum.restsupport.DefaultController
import io.barinek.continuum.users.UserController
import io.barinek.continuum.users.UserDataGateway
import org.eclipse.jetty.server.handler.HandlerList
import java.util.*

class App : BasicApp() {
    override fun getPort() = System.getenv("PORT").toInt()

    override fun handlerList(): HandlerList {
        val dataSource = DataSourceConfig().createDataSource("registration")
        val transactionManager = TransactionManager(dataSource)
        val template = JdbcTemplate(dataSource)

        val userDataGateway = UserDataGateway(template)
        val accountDataGateway = AccountDataGateway(template)

        return HandlerList().apply { // ordered
            addHandler(RegistrationController(mapper, RegistrationService(transactionManager, userDataGateway, accountDataGateway)))
            addHandler(AccountController(mapper, accountDataGateway))
            addHandler(UserController(mapper, userDataGateway))
            addHandler(ProjectControllerV1(mapper, ProjectDataGateway(template)))
            addHandler(ProjectControllerV2(mapper, ProjectDataGateway(template)))
            addHandler(DefaultController())
        }
    }
}

fun main(args: Array<String>) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    App().start()
}