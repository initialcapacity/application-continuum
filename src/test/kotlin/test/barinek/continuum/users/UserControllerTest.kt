package test.barinek.continuum.users

import io.barinek.continuum.users.UserInfo
import org.apache.http.message.BasicNameValuePair
import org.junit.Test
import test.barinek.continuum.TestControllerSupport
import test.barinek.continuum.TestScenarioSupport
import kotlin.test.assertEquals

class UserControllerTest : TestControllerSupport() {
    @Test
    fun testShow() {
        TestScenarioSupport().loadTestScenario("jacks-test-scenario")

        val response = template.get("http://localhost:8081/users", BasicNameValuePair("userId", "4765"))
        val actual = mapper.readValue(response, UserInfo::class.java)

        assertEquals(4765L, actual.id)
        assertEquals("Jack", actual.name)
        assertEquals("user info", actual.info)
    }
}