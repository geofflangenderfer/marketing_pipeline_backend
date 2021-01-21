package geofflangenderfer

import geofflangenderfer.tokens.Tokens
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI


object DB {
    private val host: String
    private val port: Int
    private val dbName: String
    private val dbUser: String
    private val dbPassword: String

    init {
        val dbUrl = System.getenv("DATABASE_URL")
        if (dbUrl != null) {
            val dbUri = URI(dbUrl)
            host = dbUri.host
            port = dbUri.port
            dbName = dbUri.path.substring(1)
            val userInfo = dbUri.userInfo.split(":")
            dbUser = userInfo[0]
            dbPassword = userInfo[1]

        }
        else {
            host = System.getenv("DB_HOST")
            port = System.getenv("DB_PORT").toInt()
            dbName = System.getenv("DB_NAME")
            dbUser = System.getenv("DB_USER")
            dbPassword = System.getenv("DB_PASSWORD")

        }
    }

    fun connect(): Database {
        return Database.connect(url="jdbc:postgresql://$host:$port/$dbName", driver="org.postgresql.Driver", user=dbUser, password=dbPassword)
    }
    fun drop() {
        transaction {
            SchemaUtils.drop(Tokens)
        }
    }
}