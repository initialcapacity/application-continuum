package test.barinek.continuum.allocations

import com.fasterxml.jackson.core.type.TypeReference
import io.barinek.continuum.allocations.AllocationController
import io.barinek.continuum.allocations.AllocationDataGateway
import io.barinek.continuum.allocations.AllocationInfo
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.restsupport.BasicApp
import io.barinek.continuum.testsupport.TestControllerSupport
import io.barinek.continuum.testsupport.TestDataSourceConfig
import io.barinek.continuum.testsupport.TestScenarioSupport
import org.apache.http.message.BasicNameValuePair
import org.eclipse.jetty.server.handler.HandlerList
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class AllocationControllerTest : TestControllerSupport() {
    private var app: BasicApp = object : BasicApp() {
        override fun getPort() = 8081

        override fun handlerList() = HandlerList().apply {
            val dataSource = TestDataSourceConfig().dataSource
            addHandler(AllocationController(mapper, AllocationDataGateway(JdbcTemplate(dataSource))))
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

        val json = "{\"projectId\":55432,\"userId\":4765,\"firstDay\":\"2014-05-16\",\"lastDay\":\"2014-05-26\"}"
        val response = template.post("http://localhost:8081/allocations", json)
        val actual = mapper.readValue(response, AllocationInfo::class.java)

        assert(actual.id > 0)
        assertEquals(55432L, actual.projectId)
        assertEquals(4765L, actual.userId)
        assertEquals(LocalDate.of(2014, 5, 16), actual.firstDay)
        assertEquals(LocalDate.of(2014, 5, 26), actual.lastDay)
        assertEquals("allocation info", actual.info)
    }

    @Test
    fun testFind() {
        TestScenarioSupport().loadTestScenario("jacks-test-scenario")

        val response = template.get("http://localhost:8081/allocations", BasicNameValuePair("projectId", "55432"))
        val list: List<AllocationInfo> = mapper.readValue(response, object : TypeReference<List<AllocationInfo>>() {})
        val actual = list.first()

        assertEquals(754L, actual.id)
        assertEquals(55432L, actual.projectId)
        assertEquals(4765L, actual.userId)
        assertEquals(LocalDate.of(2015, 5, 17), actual.firstDay)
        assertEquals(LocalDate.of(2015, 5, 18), actual.lastDay)
        assertEquals("allocation info", actual.info)
    }
}