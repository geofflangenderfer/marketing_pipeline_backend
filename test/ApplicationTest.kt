package geofflangenderfer

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.*
//import io.ktor.response.*
//import io.ktor.request.*
//import io.ktor.routing.*
import io.ktor.http.*
//import io.ktor.auth.*
import kotlin.test.*
import io.ktor.server.testing.*

class ApplicationTest {
    @Test
    fun emptyPath() {
        withTestApplication(Application::module) {
            val call = handleRequest(HttpMethod.Get, "")
            assertEquals(HttpStatusCode.OK, call.response.status())
        }
    }
    @Test
    fun validValue() {
        withTestApplication(Application::module) {
            val call = handleRequest(HttpMethod.Get, "/Snowflake")

            assertEquals("""
                {
                  "name": "Snowflake"
                }
                
            """.asJson(), call.response.content?.asJson())
            call.response.status()
            HttpStatusCode.Unauthorized
        }

    }

    //@Test
    //fun `must authorize before reading access token status /status`() {
    //    withTestApplication(Application::module) {
    //        val call = handleRequest(HttpMethod.Get, "/token")
    //        // set up body

    //        assertEquals(call.response.status(), HttpStatusCode.Unauthorized)
    //    }
    //@Test
    //fun `authorized call to /status produces access token status`() {
    //    withTestApplication(Application::module) {
    //        val call = handleRequest(HttpMethod.Get, "/token")
    //        // set up body

    //        assertEquals(call.response.status(), HttpStatusCode.Unauthorized)
    //    }
    //@Test
    //fun `must authorize before putting access token at /token`() {
    //    withTestApplication(Application::module) {
    //        val call = handleRequest(HttpMethod.Post, "/token", setBody("poop"))
    //        // set up body
    //        call.setBody("poop")

    //        assertEquals(call.response.status(), HttpStatusCode.Unauthorized)
    //    }
    //@Test
    //fun `authorized put to /token updates id provider token`() {
    //    withTestApplication(Application::module) {
    //        val call = handleRequest(HttpMethod.Post, "/token", setBody("poop"))
    //        // set up body
    //        call.setBody("poop")

    //        assertEquals(call.response.status(), HttpStatusCode.Unauthorized)
    //    }

    //}
}

