package test.barinek.continuum

import io.barinek.continuum.UserInfo
import org.junit.Test
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