package geofflangenderfer

import com.fasterxml.jackson.databind.SerializationFeature
import geofflangenderfer.tokens.Tokens
import geofflangenderfer.tokens.TokensServiceDB
import geofflangenderfer.tokens.tokenRouter
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


fun main() {
    val port = System.getenv("DB_PORT")?.toInt() ?: 8080
    println("port $port")

    val server = embeddedServer(Netty, port, module = Application::module)


    server.start()

}

fun Application.module() {

    DB.connect()

    transaction {
        SchemaUtils.create(Tokens)
    }
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    install(Authentication) {
        basic("basic") {
            realm = "Server"
            validate {
                if (it.name == "nick@circlesocialinc.com" && it.password == "+M9-fKrrzQ43q=KM") UserIdPrincipal(it.name)
                else null
            }
        }
    }
    routing {
        trace {
            application.log.debug(it.buildText())
        }

        authenticate("basic") {
            get("/login") {
                val principal = call.principal<UserIdPrincipal>()
                if (principal == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                } else {
                    call.respond(HttpStatusCode.OK)
                }

            }
            tokenRouter(TokensServiceDB())
        }
    }

}
