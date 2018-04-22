package test.barinek.continuum

import io.barinek.continuum.jdbcsupport.DataSourceConfig
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.restsupport.RestTemplate
import org.apache.http.message.BasicNameValuePair
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class FlowTest {
    val template = RestTemplate()

    lateinit var allocations: Process
    lateinit var backlog: Process
    lateinit var registration: Process
    lateinit var timesheets: Process

    @Before
    fun setUp() {
        JdbcTemplate(DataSourceConfig().createDataSource("allocations")).apply {
            execute("delete from allocations")
        }
        JdbcTemplate(DataSourceConfig().createDataSource("backlog")).apply {
            execute("delete from stories")
        }
        JdbcTemplate(DataSourceConfig().createDataSource("registration")).apply {
            execute("delete from projects")
            execute("delete from accounts")
            execute("delete from users")
        }
        JdbcTemplate(DataSourceConfig().createDataSource("timesheets")).apply {
            execute("delete from time_entries")
        }

        val userDir = System.getProperty("user.dir")

        allocations = runCommand(8881, getServices("allocations"), "java -jar $userDir/../allocations-server/build/libs/allocations-server-1.0-SNAPSHOT.jar", File(userDir))
        backlog = runCommand(8882, getServices("backlog"),"java -jar $userDir/../backlog-server/build/libs/backlog-server-1.0-SNAPSHOT.jar", File(userDir))
        registration = runCommand(8883, getServices("registration"), "java -jar $userDir/../registration-server/build/libs/registration-server-1.0-SNAPSHOT.jar", File(userDir))
        timesheets = runCommand(8884, getServices("timesheets"), "java -jar $userDir/../timesheets-server/build/libs/timesheets-server-1.0-SNAPSHOT.jar", File(userDir))
    }

    @After
    fun tearDown() {
        allocations.destroy()
        backlog.destroy()
        registration.destroy()
        timesheets.destroy()
    }

    @Test
    fun testBasicFlow() {
        Thread.sleep(4000) // sorry, waiting for servers to start

        var response: String?

        val registrationServer = "http://localhost:8883"

        response = template.get(registrationServer)
        assertEquals("Noop!", response)

        response = template.post("$registrationServer/registration", """{"name": "aUser"}""")
        val aUserId = findResponseId(response)
        assert(aUserId.toLong() > 0)

        response = template.get("$registrationServer/users", BasicNameValuePair("userId", aUserId))
        assert(!response.isNullOrEmpty())

        response = template.get("$registrationServer/accounts", BasicNameValuePair("ownerId", aUserId))
        val anAccountId = findResponseId(response)
        assert(anAccountId.toLong() > 0)

        response = template.post("$registrationServer/projects", """{"accountId":"$anAccountId","name":"aProject"}""")
        val aProjectId = findResponseId(response)
        assert(aProjectId.toLong() > 0)

        response = template.get("$registrationServer/projects", BasicNameValuePair("accountId", anAccountId))
        assert(!response.isNullOrEmpty())

        ///

        val allocationsServer = "http://localhost:8881"

        response = template.get(allocationsServer)
        assertEquals("Noop!", response)

        response = template.post("$allocationsServer/allocations", """{"projectId":$aProjectId,"userId":$aUserId,"firstDay":"2015-05-17","lastDay":"2015-05-26"}""")
        val anAllocationId = findResponseId(response)
        assert(anAllocationId.toLong() > 0)

        response = template.get("$allocationsServer/allocations", BasicNameValuePair("projectId", aProjectId))
        assert(!response.isNullOrEmpty())


        val backlogServer = "http://localhost:8882"

        response = template.get(backlogServer)
        assertEquals("Noop!", response)

        response = template.post("$backlogServer/stories", """{"projectId":$aProjectId,"name":"A story"}""")
        val aStoryId = findResponseId(response)
        assert(aStoryId.toLong() > 0)

        response = template.get("$backlogServer/stories", BasicNameValuePair("projectId", aProjectId))
        assert(!response.isNullOrEmpty())


        val timesheetsServer = "http://localhost:8884"

        response = template.get(timesheetsServer)
        assertEquals("Noop!", response)

        response = template.post("$timesheetsServer/time-entries", """{"projectId":$aProjectId,"userId":$aUserId,"date":"2015-05-17","hours":"8"}""")
        val aTimeEntryId = findResponseId(response)
        assert(aTimeEntryId.toLong() > 0)

        response = template.get("$timesheetsServer/time-entries", BasicNameValuePair("userId", aUserId))
        assert(!response.isNullOrEmpty())
    }

    /// Test Support

    private fun getServices(name:String) = "{ \"p-mysql\": [ { \"credentials\": { \"jdbcUrl\": \"jdbc:mysql://localhost:3306/${name}_test?user=uservices&password=uservices&useTimezone=true&serverTimezone=UTC\", \"name\": \"$name\"} } ] }"

    private fun findResponseId(response: String) = Regex("id\":(\\d+),").find(response)?.groupValues!![1]

    private fun runCommand(port: Int, services: String, command: String, workingDir: File): Process {
        val builder = ProcessBuilder(*command.split(" ").toTypedArray())
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
        builder.environment()["PORT"] = port.toString()
        builder.environment()["VCAP_SERVICES"] = services
        builder.environment()["REGISTRATION_SERVER_ENDPOINT"] = "http://localhost:8883"
        return builder.start()
    }
}

