package test.barinek.continuum.allocations

import com.fasterxml.jackson.core.type.TypeReference
import io.barinek.continuum.allocations.AllocationInfo
import org.apache.http.message.BasicNameValuePair
import org.junit.Test
import test.barinek.continuum.TestControllerSupport
import test.barinek.continuum.TestScenarioSupport
import java.time.LocalDate
import kotlin.test.assertEquals

class AllocationControllerTest : TestControllerSupport() {
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