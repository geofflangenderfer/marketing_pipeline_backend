package geofflangenderfer

import geofflangenderfer.tokens.Tokens
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


object DB {
    private val host = System.getenv("DB_HOST")
    private val port = System.getenv("DB_PORT")
    private val dbName = System.getenv("DB_NAME")
    private val dbUser = System.getenv("DB_USER")
    private val dbPassword = System.getenv("DB_PASSWORD")

    fun connect(): Database {
        return Database.connect(url="jdbc:postgresql://$host:$port/$dbName", driver="org.postgresql.Driver", user=dbUser, password=dbPassword)
    }
    fun drop() {
        transaction {
            SchemaUtils.drop(Tokens)
        }
    }
}