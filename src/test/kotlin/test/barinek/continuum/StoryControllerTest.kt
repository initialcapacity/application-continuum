package test.barinek.continuum

import com.fasterxml.jackson.core.type.TypeReference
import io.barinek.continuum.StoryInfo
import org.apache.http.message.BasicNameValuePair
import org.junit.Test
import kotlin.test.assertEquals

class StoryControllerTest : TestControllerSupport() {
    @Test
    fun testCreate() {
        TestScenarioSupport().loadTestScenario("jacks-test-scenario")

        val json = "{\"projectId\":55432,\"name\":\"An epic story\"}"
        val response = template.post("http://localhost:8081/stories", json)
        val actual = mapper.readValue(response, StoryInfo::class.java)

        assert(actual.id > 0)
        assertEquals(55432L, actual.projectId)
        assertEquals("An epic story", actual.name)
        assertEquals("story info", actual.info)
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