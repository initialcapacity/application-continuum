package io.barinek.continuum

import org.eclipse.jetty.server.handler.HandlerList
import java.util.*

class App : BasicApp() {
    override fun getPort() = System.getenv("PORT").toInt()

    override fun handlerList(transactionManager: TransactionManager, template: JdbcTemplate): HandlerList {
        val userDataGateway = UserDataGateway(template)
        val accountDataGateway = AccountDataGateway(template)

        return HandlerList().apply { // ordered

            addHandler(RegistrationController(mapper, RegistrationService(transactionManager, userDataGateway, accountDataGateway)))
            addHandler(AccountController(mapper, accountDataGateway))
            addHandler(AllocationController(mapper, AllocationDataGateway(template)))
            addHandler(UserController(mapper, userDataGateway))
            addHandler(ProjectController(mapper, ProjectDataGateway(template)))
            addHandler(StoryController(mapper, StoryDataGateway(template)))
            addHandler(TimeEntryController(mapper, TimeEntryDataGateway(template)))
        }
    }
}

fun main(args: Array<String>) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    App().start()
}