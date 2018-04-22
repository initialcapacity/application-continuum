package test.barinek.continuum.accounts

import io.barinek.continuum.users.UserInfo
import org.junit.Test
import test.barinek.continuum.TestControllerSupport
import test.barinek.continuum.TestScenarioSupport
import kotlin.test.assertEquals

class RegistrationControllerTest : TestControllerSupport() {
    @Test
    fun testRegister() {
        TestScenarioSupport().loadTestScenario("jacks-test-scenario")

        val registrationResponse = template.post("http://localhost:8081/registration", "{\"name\":\"aUser\"}")
        val actual = mapper.readValue(registrationResponse, UserInfo::class.java)

        assert(actual.id > 0)
        assertEquals("aUser", actual.name)
        assertEquals("registration info", actual.info)
    }
}