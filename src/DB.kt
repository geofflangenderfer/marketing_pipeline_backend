package geofflangenderfer

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object Tokens: IntIdTable() {
    val user = varchar("user", 255)
    val access_token = varchar("access_token", 255)
    val expire_time = integer("expire_time_unix")
    val refresh_token = varchar("refresh_token", 255)
    val id_provider = varchar("id_provider", 255).uniqueIndex()
}

object DB {
    private val host = "localhost"
    private val port = 5555
    private val dbName = "tokens"
    private val dbUser = "backend"
    private val dbPassword = "backend"

    fun connect(): Database {
        return Database.connect(url="jdbc:postgresql://$host:$port/$dbName", driver="org.postgresql.Driver", user=dbUser, password=dbPassword)
    }
    fun drop() {
        transaction {
            SchemaUtils.drop(Tokens)
        }
    }
}