package test.barinek.continuum.backlog

import com.fasterxml.jackson.core.type.TypeReference
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.barinek.continuum.backlog.*
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
import kotlin.test.assertEquals

class StoryControllerTest : TestControllerSupport() {
    val client = mock<ProjectClient>()

    internal var app: BasicApp = object : BasicApp() {
        override fun getPort() = 8081

        override fun handlerList() = HandlerList().apply {
            val dataSource = TestDataSourceConfig().dataSource
            addHandler(StoryController(mapper, StoryDataGateway(JdbcTemplate(dataSource)), client))
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

        whenever(client.getProject(any())).thenReturn(ProjectInfo(true))

        val json = "{\"projectId\":55432,\"name\":\"An epic story\"}"
        val response = template.post("http://localhost:8081/stories", json)
        val actual = mapper.readValue(response, StoryInfo::class.java)

        assert(actual.id > 0)
        assertEquals(55432L, actual.projectId)
        assertEquals("An epic story", actual.name)
        assertEquals("story info", actual.info)
    }

    @Test
    fun testFailedCreate() {
        TestScenarioSupport().loadTestScenario("jacks-test-scenario")

        whenever(client.getProject(any())).thenReturn(ProjectInfo(false))

        val json = "{\"projectId\":55432,\"name\":\"An epic story\"}"
        val response = template.post("http://localhost:8081/stories", json)
        assert(response.isBlank())
    }

    @Test
    fun testFind() {
        TestScenarioSupport().loadTestScenario("jacks-test-scenario")

        val response = template.get("http://localhost:8081/stories", BasicNameValuePair("projectId", "55432"))
        val stories: List<StoryInfo> = mapper.readValue(response, object : TypeReference<List<StoryInfo>>() {})
        val actual = stories.first()

        assertEquals(876L, actual.id)
        assertEquals(55432L, actual.projectId)
        assertEquals("An epic story", actual.name)
        assertEquals("story info", actual.info)
    }
}