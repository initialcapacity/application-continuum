package test.barinek.continuum.projects

import com.fasterxml.jackson.core.type.TypeReference
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.projects.ProjectController
import io.barinek.continuum.projects.ProjectDataGateway
import io.barinek.continuum.projects.ProjectInfo
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
import kotlin.test.assertFalse

class ProjectControllerTest : TestControllerSupport() {
    private var app: BasicApp = object : BasicApp() {
        override fun getPort() = 8081

        override fun handlerList() = HandlerList().apply {
            val dataSource = TestDataSourceConfig().dataSource
            addHandler(ProjectController(mapper, ProjectDataGateway(JdbcTemplate(dataSource))))
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

        val json = "{\"accountId\":1673,\"name\":\"aProject\"}"
        val response = template.post("http://localhost:8081/projects", json)
        val actual = mapper.readValue(response, ProjectInfo::class.java)

        assertEquals(1673L, actual.accountId)
        assertEquals("aProject", actual.name)
        assertEquals("project info", actual.info)
        assert(actual.active)
    }

    @Test
    fun testList() {
        TestScenarioSupport().loadTestScenario("jacks-test-scenario")

        val response = template.get("http://localhost:8081/projects", BasicNameValuePair("accountId", "1673"))
        val list: List<ProjectInfo> = mapper.readValue(response, object : TypeReference<List<ProjectInfo>>() {})
        val actual = list.first()

        assertEquals(55432L, actual.id)
        assertEquals(1673L, actual.accountId)
        assertEquals("Flagship", actual.name)
        assertEquals("project info", actual.info)
        assert(actual.active)
    }

    @Test
    fun testGet() {
        TestScenarioSupport().loadTestScenario("jacks-test-scenario")

        val response = template.get("http://localhost:8081/project", BasicNameValuePair("projectId", "55431"))
        val actual = mapper.readValue(response, ProjectInfo::class.java)

        assertEquals(55431L, actual.id)
        assertEquals(1673L, actual.accountId)
        assertEquals("Hovercraft", actual.name)
        assertEquals("project info", actual.info)
        assertFalse(actual.active)
    }

    @Test
    fun testNotFound() {
        TestScenarioSupport().loadTestScenario("jacks-test-scenario")

        val response = template.get("http://localhost:8081/project", BasicNameValuePair("projectId", "5280"))
        assert(response.isBlank())
    }
}