package test.barinek.continuum

import com.fasterxml.jackson.core.type.TypeReference
import io.barinek.continuum.AccountInfo
import org.apache.http.message.BasicNameValuePair
import org.junit.Test
import kotlin.test.assertEquals

class AccountControllerTest : TestControllerSupport() {
    @Test
    fun testFind() {
        TestScenarioSupport().loadTestScenario("jacks-test-scenario")

        val ownerId = BasicNameValuePair("ownerId", "4765")
        val response = template.get("http://localhost:8081/accounts", ownerId)
        val list: List<AccountInfo> = mapper.readValue(response, object : TypeReference<List<AccountInfo>>() {})
        val actual = list.first()

        assertEquals(1673L, actual.id)
        assertEquals(4765L, actual.ownerId)
        assertEquals("Jack's account", actual.name)
        assertEquals("account info", actual.info)
    }
}