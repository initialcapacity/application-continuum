package test.barinek.continuum.projects

import com.fasterxml.jackson.core.type.TypeReference
import io.barinek.continuum.jdbcsupport.DataSourceConfig
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.projects.ProjectControllerV1
import io.barinek.continuum.projects.ProjectDataGateway
import io.barinek.continuum.projects.ProjectInfoV1
import io.barinek.continuum.restsupport.BasicApp
import io.barinek.continuum.testsupport.TestControllerSupport
import io.barinek.continuum.testsupport.TestScenarioSupport
import org.apache.http.message.BasicNameValuePair
import org.eclipse.jetty.server.handler.HandlerList
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ProjectControllerV1Test : TestControllerSupport() {
    val dataSource = DataSourceConfig().createDataSource("registration")

    internal var app: BasicApp = object : BasicApp() {
        override fun getPort() = 8081

        override fun handlerList() = HandlerList().apply {
            addHandler(ProjectControllerV1(mapper, ProjectDataGateway(JdbcTemplate(dataSource))))
        }
    }

    @Before
    fun setUp() {
        JdbcTemplate(dataSource).apply {
            execute("delete from projects")
            execute("delete from accounts")
            execute("delete from users")
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

        val json = "{\"accountId\":1673,\"name\":\"aProject\"}"
        val response = template.post("http://localhost:8081/projects", "application/vnd.appcontinuum.v1+json", json)
        val actual = mapper.readValue(response, ProjectInfoV1::class.java)

        assertEquals(1673L, actual.accountId)
        assertEquals("aProject", actual.name)
        assertEquals("project info", actual.info)
        assert(actual.active)
    }

    @Test
    fun testList() {
        TestScenarioSupport(dataSource).loadTestScenario("jacks-test-scenario")

        val response = template.get("http://localhost:8081/projects", "application/vnd.appcontinuum.v1+json", BasicNameValuePair("accountId", "1673"))
        val list: List<ProjectInfoV1> = mapper.readValue(response, object : TypeReference<List<ProjectInfoV1>>() {})
        val actual = list.first()

        assertEquals(55432L, actual.id)
        assertEquals(1673L, actual.accountId)
        assertEquals("Flagship", actual.name)
        assertEquals("project info", actual.info)
        assert(actual.active)
    }

    @Test
    fun testGet() {
        TestScenarioSupport(dataSource).loadTestScenario("jacks-test-scenario")

        val response = template.get("http://localhost:8081/project", "application/vnd.appcontinuum.v1+json", BasicNameValuePair("projectId", "55431"))
        val actual = mapper.readValue(response, ProjectInfoV1::class.java)

        assertEquals(55431L, actual.id)
        assertEquals(1673L, actual.accountId)
        assertEquals("Hovercraft", actual.name)
        assertEquals("project info", actual.info)
        assertFalse(actual.active)
    }

    @Test
    fun testNotFound() {
        TestScenarioSupport(dataSource).loadTestScenario("jacks-test-scenario")

        val response = template.get("http://localhost:8081/project", "application/vnd.appcontinuum.v1+json", BasicNameValuePair("projectId", "5280"))
        assert(response.isBlank())
    }
}