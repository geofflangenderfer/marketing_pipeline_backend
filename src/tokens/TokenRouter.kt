package geofflangenderfer.tokens

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.tokenRouter(tokenService: TokensService) {
    route("/tokens") {
        post {
            val principal = call.principal<UserIdPrincipal>()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val body = call.receive<TokenPost>()
                val id = tokenService.create(body)
                // should I
                call.respond(HttpStatusCode.Created)//, id) should I send this along too?
            }
        }
        get {
            val principal = call.principal<UserIdPrincipal>()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val providers = tokenService.all()
                call.respond(providers)
            }
        }
        route("/expired") {
            get {
                val principal = call.principal<UserIdPrincipal>()
                if (principal == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                } else {
                    val providers = tokenService.expired()
                    call.respond(providers)
                }
            }
        }
        route("/{id}") {
            put {
                val principal = call.principal<UserIdPrincipal>()
                if (principal == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                } else {
                    val body = call.receive<TokenPost>()
                    val id = tokenService.update(body)
                    call.respond(HttpStatusCode.Created)//, id) should I send this?
                }
            }
        }
    }
}