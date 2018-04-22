package test.barinek.continuum

import com.fasterxml.jackson.core.type.TypeReference
import io.barinek.continuum.ProjectInfo
import org.apache.http.message.BasicNameValuePair
import org.junit.Test
import kotlin.test.assertEquals

class ProjectControllerTest : TestControllerSupport() {
    @Test
    fun testCreate() {
        TestScenarioSupport().loadTestScenario("jacks-test-scenario")

        val json = "{\"accountId\":1673,\"name\":\"aProject\"}"
        val response = template.post("http://localhost:8081/projects", json)
        val actual = mapper.readValue(response, ProjectInfo::class.java)

        assertEquals(1673L, actual.accountId)
        assertEquals("aProject", actual.name)
        assertEquals("project info", actual.info)
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
    }
}