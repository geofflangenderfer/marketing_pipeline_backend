package geofflangenderfer.tokens

import geofflangenderfer.Tokens
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*


interface TokensService {
    suspend fun create(tokenPost: TokenPost): Int
    suspend fun update(tokenPost: TokenPost): Int
    suspend fun expired(): List<TokenPost>
    suspend fun all(): List<TokenPost>
    suspend fun getLongTermAccessToken()
}

class TokensServiceDB : TokensService {
    override suspend fun create(tokenPost: TokenPost): Int {
        val id = transaction {
            addLogger(StdOutSqlLogger)

            Tokens.insertAndGetId { token ->
                token[Tokens.user] = tokenPost.user
                token[Tokens.access_token] = tokenPost.access_token
                token[Tokens.expire_time] = tokenPost.expire_time
                token[Tokens.refresh_token] = tokenPost.refresh_token
                token[Tokens.id_provider] = tokenPost.id_provider
            }
        }
        return id.value
    }

    override suspend fun update(tokenPost: TokenPost): Int {
        return transaction {
            addLogger(StdOutSqlLogger)

            Tokens.update({ Tokens.id_provider eq tokenPost.id_provider}) {
                it[Tokens.access_token] = tokenPost.access_token
                it[Tokens.expire_time] = tokenPost.expire_time
                it[Tokens.refresh_token] = tokenPost.refresh_token
            }
        }
    }

    override suspend fun expired(): List<TokenPost> {
        return transaction {
            addLogger(StdOutSqlLogger)
            Tokens.selectAll()
               .filter { row ->
                   val expireTime = row[Tokens.expire_time].toLong()
                   val now = Instant.now().epochSecond
                   expireTime <= now
                }
                .map { row -> row.asToken() }
        }
    }
    override suspend fun all(): List<TokenPost> {
        return transaction {
            addLogger(StdOutSqlLogger)
            Tokens.selectAll()
                .map { row -> row.asToken() }
        }
    }

    override suspend fun getLongTermAccessToken() {
        //https://developers.facebook.com/docs/facebook-login/access-tokens/refreshing
        //update db row.access_token
    }
}

data class TokenPost(
    val user: String = "",
    val access_token: String = "",
    val expire_time: Int = -1,
    val refresh_token: String = "",
    val id_provider: String = ""
)

fun ResultRow.asToken() = TokenPost(
    this[Tokens.user],
    this[Tokens.access_token],
    this[Tokens.expire_time],
    this[Tokens.refresh_token],
    this[Tokens.id_provider]
)