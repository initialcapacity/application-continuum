package test.barinek.continuum.projects

import com.fasterxml.jackson.core.type.TypeReference
import io.barinek.continuum.jdbcsupport.DataSourceConfig
import io.barinek.continuum.jdbcsupport.JdbcTemplate
import io.barinek.continuum.projects.ProjectControllerV2
import io.barinek.continuum.projects.ProjectDataGateway
import io.barinek.continuum.projects.ProjectInfoV2
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

class ProjectControllerV2Test : TestControllerSupport() {
    val dataSource = DataSourceConfig().createDataSource("registration")

    internal var app: BasicApp = object : BasicApp() {
        override fun getPort() = 8081

        override fun handlerList() = HandlerList().apply {
            addHandler(ProjectControllerV2(mapper, ProjectDataGateway(JdbcTemplate(dataSource))))
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

        val json = "{\"accountId\":1673,\"name\":\"aProject\",\"active\":true,\"funded\":true}"
        val response = template.post("http://localhost:8081/projects", "application/vnd.appcontinuum.v2+json", json)
        val actual = mapper.readValue(response, ProjectInfoV2::class.java)

        assertEquals(1673L, actual.accountId)
        assertEquals("aProject", actual.name)
        assertEquals("project info", actual.info)
        assert(actual.active)
        assert(actual.funded)
    }

    @Test
    fun testList() {
        TestScenarioSupport(dataSource).loadTestScenario("jacks-test-scenario")

        val response = template.get("http://localhost:8081/projects", "application/vnd.appcontinuum.v2+json", BasicNameValuePair("accountId", "1673"))
        val list: List<ProjectInfoV2> = mapper.readValue(response, object : TypeReference<List<ProjectInfoV2>>() {})
        val actual = list.first()

        assertEquals(55432L, actual.id)
        assertEquals(1673L, actual.accountId)
        assertEquals("Flagship", actual.name)
        assertEquals("project info", actual.info)
        assert(actual.active)
        assertFalse(actual.funded)
    }

    @Test
    fun testGet() {
        TestScenarioSupport(dataSource).loadTestScenario("jacks-test-scenario")

        val response = template.get("http://localhost:8081/project", "application/vnd.appcontinuum.v2+json", BasicNameValuePair("projectId", "55432"))
        val actual = mapper.readValue(response, ProjectInfoV2::class.java)

        assertEquals(55432L, actual.id)
        assertEquals(1673L, actual.accountId)
        assertEquals("Flagship", actual.name)
        assertEquals("project info", actual.info)
        assert(actual.active)
        assertFalse(actual.funded)
    }

    @Test
    fun testNotFound() {
        TestScenarioSupport(dataSource).loadTestScenario("jacks-test-scenario")

        val response = template.get("http://localhost:8081/project", "application/vnd.appcontinuum.v2+json", BasicNameValuePair("projectId", "5280"))
        assert(response.isBlank())
    }
}