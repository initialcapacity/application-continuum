package test.barinek.continuum.timesheets

import com.fasterxml.jackson.core.type.TypeReference
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.barinek.continuum.jdbcsupport.DataSourceConfig
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.restsupport.BasicApp
import io.barinek.continuum.testsupport.TestControllerSupport
import io.barinek.continuum.testsupport.TestScenarioSupport
import io.barinek.continuum.timesheets.*
import org.apache.http.message.BasicNameValuePair
import org.eclipse.jetty.server.handler.HandlerList
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class TimeEntryControllerTest : TestControllerSupport() {
    val dataSource = DataSourceConfig().createDataSource("timesheets")
    val client = mock<ProjectClient>()

    private var app: BasicApp = object : BasicApp() {
        override fun getPort() = 8081

        override fun handlerList() = HandlerList().apply {
            addHandler(TimeEntryController(mapper, TimeEntryDataGateway(JdbcTemplate(dataSource)), client))
        }
    }

    @Before
    fun setUp() {
        JdbcTemplate(dataSource).apply {
            execute("delete from time_entries")
        }
        app.start()
    }

    @After
    fun tearDown() {
        app.stop()
    }

    @Test
    fun testCreate() {
        TestScenarioSupport(dataSource).loadTestScenario("jacks-test-scenario")

        whenever(client.getProject(any())).thenReturn(ProjectInfo(true, true))

        val json = "{\"projectId\":55432,\"userId\":4765,\"date\":\"2015-05-17\",\"hours\":8}"
        val response = template.post("http://localhost:8081/time-entries", "application/json", json)

        val actual = mapper.readValue(response, TimeEntryInfo::class.java)

        assert(actual.id > 0)
        assertEquals(55432L, actual.projectId)
        assertEquals(4765L, actual.userId)
        assertEquals(8, actual.hours)
    }

    @Test
    fun testFailedCreate() {
        TestScenarioSupport(dataSource).loadTestScenario("jacks-test-scenario")

        whenever(client.getProject(any())).thenReturn(ProjectInfo(true, false))

        val json = "{\"projectId\":55432,\"userId\":4765,\"date\":\"2015-05-17\",\"hours\":8}"
        val response = template.post("http://localhost:8081/time-entries", "application/json", json)
        assert(response.isBlank())
    }

    @Test
    fun testFind() {
        TestScenarioSupport(dataSource).loadTestScenario("jacks-test-scenario")

        val response = template.get("http://localhost:8081/time-entries", "application/json", BasicNameValuePair("userId", "4765"))
        val stories: List<TimeEntryInfo> = mapper.readValue(response, object : TypeReference<List<TimeEntryInfo>>() {})
        val actual = stories.first()

        assertEquals(1534L, actual.id)
        assertEquals(55432L, actual.projectId)
        assertEquals(4765L, actual.userId)
        assertEquals(LocalDate.of(2015, 5, 17), actual.date)
        assertEquals(5, actual.hours)
    }
}