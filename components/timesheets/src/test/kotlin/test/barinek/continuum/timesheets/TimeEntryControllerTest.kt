package test.barinek.continuum.timesheets

import com.fasterxml.jackson.core.type.TypeReference
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.restsupport.BasicApp
import io.barinek.continuum.testsupport.TestControllerSupport
import io.barinek.continuum.testsupport.TestDataSourceConfig
import io.barinek.continuum.testsupport.TestScenarioSupport
import io.barinek.continuum.timesheets.TimeEntryController
import io.barinek.continuum.timesheets.TimeEntryDataGateway
import io.barinek.continuum.timesheets.TimeEntryInfo
import org.apache.http.message.BasicNameValuePair
import org.eclipse.jetty.server.handler.HandlerList
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class TimeEntryControllerTest : TestControllerSupport() {
    private var app: BasicApp = object : BasicApp() {
        override fun getPort() = 8081

        override fun handlerList() = HandlerList().apply {
            val dataSource = TestDataSourceConfig().dataSource
            addHandler(TimeEntryController(mapper, TimeEntryDataGateway(JdbcTemplate(dataSource))))
        }
    }

    @Before
    fun setUp() {
        app.start()
    }

    @After
    fun tearDown() {
        app.stop()
    }

    @Test
    fun testCreate() {
        TestScenarioSupport().loadTestScenario("jacks-test-scenario")

        val json = "{\"projectId\":55432,\"userId\":4765,\"date\":\"2015-05-17\",\"hours\":8}"
        val response = template.post("http://localhost:8081/time-entries", json)

        val actual = mapper.readValue(response, TimeEntryInfo::class.java)

        assert(actual.id > 0)
        assertEquals(55432L, actual.projectId)
        assertEquals(4765L, actual.userId)
        assertEquals(8, actual.hours)
    }

    @Test
    fun testFind() {
        TestScenarioSupport().loadTestScenario("jacks-test-scenario")

        val response = template.get("http://localhost:8081/time-entries", BasicNameValuePair("userId", "4765"))
        val stories: List<TimeEntryInfo> = mapper.readValue(response, object : TypeReference<List<TimeEntryInfo>>() {})
        val actual = stories.first()

        assertEquals(1534L, actual.id)
        assertEquals(55432L, actual.projectId)
        assertEquals(4765L, actual.userId)
        assertEquals(LocalDate.of(2015, 5, 17), actual.date)
        assertEquals(5, actual.hours)
    }
}