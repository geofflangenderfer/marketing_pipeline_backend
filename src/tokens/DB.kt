package geofflangenderfer.tokens

import org.jetbrains.exposed.dao.id.IntIdTable

object Tokens: IntIdTable() {
    val user = varchar("user", 255)
    val access_token = varchar("access_token", 255)
    val expire_time = integer("expire_time_unix")
    val refresh_token = varchar("refresh_token", 255)
    val id_provider = varchar("id_provider", 255).uniqueIndex()
}
