package geofflangenderfer.tokens

import com.fasterxml.jackson.databind.JsonNode
import geofflangenderfer.DB
import geofflangenderfer.Tokens
import geofflangenderfer.asJson
import geofflangenderfer.mainModule
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import com.google.gson.GsonBuilder
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import java.time.Instant


class TokensTest {
    @BeforeEach
    fun cleanup() {
        DB.connect()
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.drop(Tokens)
            SchemaUtils.create(Tokens)
        }
    }

    // "/login"
    @Test
    fun `unauthorized GET login returns HTTP Unauthorized`() {
        withTestApplication(Application::mainModule) {
            val call = handleRequest(HttpMethod.Get, "/login") {
                val encodedPassword: String = Base64
                    .getEncoder()
                    .encodeToString("nick@circlesocialinc.com:00+M9-fKrrzQ43q=KM".toByteArray())
                addHeader(HttpHeaders.Authorization, "Basic $encodedPassword")

            }
            assertEquals(HttpStatusCode.Unauthorized, call.response.status())

        }

    }

    @Test
    fun `authorized GET login returns HTTP OK`() {
        withTestApplication(Application::mainModule) {
            val call = handleRequest(HttpMethod.Get, "/login") {
                val encodedPassword: String = Base64
                    .getEncoder()
                    .encodeToString("nick@circlesocialinc.com:+M9-fKrrzQ43q=KM".toByteArray())
                addHeader(HttpHeaders.Authorization, "Basic $encodedPassword")

            }
            assertEquals(HttpStatusCode.OK, call.response.status())

        }
    }


    // "/tokens"

    @Test
    fun `unauthorized GET tokens returns HTTP Unauthorized`() {
        // setup
        runBlocking { seedTokens(expiredTokens) }
            // act
            withTestApplication(Application::mainModule) {
                val call = handleRequest(HttpMethod.Get, "/tokens") {
                    val encodedPassword: String = Base64
                        .getEncoder()
                        //wrong password
                        .encodeToString("nick@circlesocialinc.com:00+M9-fKrrzQ43q=KM".toByteArray())
                    addHeader(HttpHeaders.Authorization, "Basic $encodedPassword")
                }

                // assert
                assertEquals(HttpStatusCode.Unauthorized, call.response.status())
            }
    }
    @Test
    fun `authorized GET tokens returns List of all tokens`() {
        // setup
        runBlocking { seedTokens(oneActiveToken) }

        // act
        withTestApplication(Application::mainModule) {
            val call = handleRequest(HttpMethod.Get, "/tokens") {
                val encodedPassword: String = Base64
                    .getEncoder()
                    .encodeToString("nick@circlesocialinc.com:+M9-fKrrzQ43q=KM".toByteArray())
                addHeader(HttpHeaders.Authorization, "Basic $encodedPassword")
            }

            // assert
            val expectedExpiredTokens: List<TokenPost> = oneActiveToken.map { it.asTokenPost() }

            val gson = GsonBuilder().create()
            val actualExpiredTokens = gson.fromJson(call.response.content, Array<TokenPost>::class.java).toList()

            assertEquals(expectedExpiredTokens, actualExpiredTokens)
        }

    }
    @Test
    fun `authorized GET tokens expired returns List of expired tokens`() {
        // setup
        runBlocking { seedTokens(oneActiveToken) }

        // act
        withTestApplication(Application::mainModule) {
            val call = handleRequest(HttpMethod.Get, "/tokens/expired") {
                val encodedPassword: String = Base64
                    .getEncoder()
                    .encodeToString("nick@circlesocialinc.com:+M9-fKrrzQ43q=KM".toByteArray())
                addHeader(HttpHeaders.Authorization, "Basic $encodedPassword")
            }

            // assert
            val expectedExpiredTokens: List<TokenPost> = oneActiveToken
                .map { it.asTokenPost() }
                .filter {
                    val expireTime = it.expire_time
                    val now = Instant.now().epochSecond
                    expireTime <= now
                }

            val gson = GsonBuilder().create()
            val actualExpiredTokens = gson.fromJson(call.response.content, Array<TokenPost>::class.java).toList()

            assertEquals(expectedExpiredTokens, actualExpiredTokens)
        }
    }

    @Test
    fun `unauthorized PUT tokens {id} returns HTTP Unauthorized`() {
        withTestApplication(Application::mainModule) {
            val call = handleRequest(HttpMethod.Put, "/tokens") {
                val encodedPassword: String = Base64
                    .getEncoder()
                    // wrong password
                    .encodeToString("nick@circlesocialinc.com:00+M9-fKrrzQ43q=KM".toByteArray())
                addHeader(HttpHeaders.Authorization, "Basic $encodedPassword")
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.withCharset(Charsets.UTF_8).toString())
                val newToken = """
                {
                    "user": "nick@circlesocialinc.com",
                    "access_token": "ABC123NewToken",
                    "expire_time": "1641013200",
                    "refresh_token": "ABC321NewToken",
                    "id_provider": "facebook"
                }
                """.asJson()
                setBody(newToken.toString())
            }
            assertEquals(HttpStatusCode.Unauthorized, call.response.status())
        }
    }
    @Test
    fun `authorized PUT tokens {id} returns HTTP Unauthorized`() {
        withTestApplication(Application::mainModule) {
            val call = handleRequest(HttpMethod.Put, "/tokens") {
                val encodedPassword: String = Base64
                    .getEncoder()
                    .encodeToString("nick@circlesocialinc.com:+M9-fKrrzQ43q=KM".toByteArray())
                addHeader(HttpHeaders.Authorization, "Basic $encodedPassword")
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.withCharset(Charsets.UTF_8).toString())
                val newToken = """
                {
                    "user": "nick@circlesocialinc.com",
                    "access_token": "ABC123NewToken",
                    "expire_time": "1641013200",
                    "refresh_token": "ABC321NewToken",
                    "id_provider": "facebook"
                }
                """.asJson()
                setBody(newToken.toString())
            }
            assertEquals(HttpStatusCode.Created, call.response.status())
        }
    }


}
//fun TestApplicationEngine.postToken(token: JsonNode): TestApplicationCall {
//
//    val encodedPassword: String = Base64
//        .getEncoder()
//        .encodeToString("nick@circlesocialinc.com:+M9-fKrrzQ43q=KM".toByteArray())
//
//
//    return handleRequest(HttpMethod.Post, "/tokens") {
//        addHeader(HttpHeaders.Authorization, "Basic $encodedPassword")
//        addHeader(HttpHeaders.ContentType, "application/json; utf-8")
//        setBody(token.toString())
//    }
//}
fun JsonNode.asTokenPost(): TokenPost {
    return TokenPost(
        this["user"].toString(),
        this["access_token"].toString(),
        this["expire_time"].toString().toInt(),
        this["refresh_token"].toString(),
        this["id_provider"].toString()
    )

}
private val activeFb = """
                {
                    "user": "nick@circlesocialinc.com",
                    "access_token": "ABC123NewToken",
                    "expire_time": 1641013200,
                    "refresh_token": "ABC321NewToken",
                    "id_provider": "facebook"
                }
            
        """.asJson()
private val expiredFb = """
                {
                    "user": "nick@circlesocialinc.com",
                    "access_token": "ABC123NewToken",
                    "expire_time": 1609488000,
                    "refresh_token": "ABC321NewToken",
                    "id_provider": "facebook"
                }
            
        """.asJson()
private val expiredGoogleAds = """
                {
                    "user": "nick@circlesocialinc.com",
                    "access_token": "ABC123NewToken",
                    "expire_time": 1609488000,
                    "refresh_token": "ABC321NewToken",
                    "id_provider": "google_ads"
                }
            
        """.asJson()
private val expiredGoogleAnalytics = """
                {
                    "user": "nick@circlesocialinc.com",
                    "access_token": "ABC123NewToken",
                    "expire_time": 1609488000,
                    "refresh_token": "ABC321NewToken",
                    "id_provider": "google_analytics"
                }
            
        """.asJson()
private val expiredGoogleMyBusiness = """
                {
                    "user": "nick@circlesocialinc.com",
                    "access_token": "ABC123NewToken",
                    "expire_time": 1609488000,
                    "refresh_token": "ABC321NewToken",
                    "id_provider": "google_my_business"
                }
            
        """.asJson()
private val expiredNewsletters = """
                {
                    "user": "nick@circlesocialinc.com",
                    "access_token": "ABC123NewToken",
                    "expire_time": 1609488000,
                    "refresh_token": "ABC321NewToken",
                    "id_provider": "newsletters"
                }
            
        """.asJson()
private val expiredTokens = listOf(expiredFb, expiredGoogleAds, expiredGoogleAnalytics, expiredGoogleMyBusiness, expiredNewsletters)
private val oneActiveToken = listOf(activeFb, expiredGoogleAds, expiredGoogleAnalytics, expiredGoogleMyBusiness, expiredNewsletters)
private suspend fun seedTokens(tokens: List<JsonNode>) {
    val dbController = TokensServiceDB()
    for (token in tokens) {
        dbController.create(token.asTokenPost())
    }
}
