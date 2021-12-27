package logan.pickpocket

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource
import logan.pickpocket.main.PickpocketPlugin
import logan.pickpocket.user.PickpocketUser

class PickpocketDatabase(private val serverName: String, val user: String, private val password: String) {
    private val dataSource = MysqlConnectionPoolDataSource()

    init {
        // Initialize the data source.
        dataSource.let {
            it.serverName = serverName
            it.user = user
            it.password = password
        }
        PickpocketPlugin.log("Database connection successful.")
        createDatabaseAndTables()
    }

    private fun createDatabaseAndTables() {
        dataSource.connection.use {
            if (!it.isValid(1000)) return
            val statement = it.createStatement()
            statement.addBatch("CREATE DATABASE IF NOT EXISTS pickpocket")
            statement.addBatch("USE pickpocket")
            statement.addBatch(
                """
                    CREATE TABLE IF NOT EXISTS users(
                        uuid VARCHAR(255),
                        name VARCHAR(255),
                        steals INT,
                        PRIMARY KEY (uuid)
                    )
                """.trimIndent()
            )
            statement.executeBatch()
        }
    }

    fun addUser(user: PickpocketUser) {
        dataSource.connection.use {
            if (!it.isValid(1000)) return
            it.prepareStatement("USE pickpocket").execute()
            it.prepareStatement("INSERT INTO users VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE steals = steals").also { ps ->
                ps.setString(1, user.uuid.toString())
                ps.setString(2, user.bukkitPlayer!!.name)
                ps.setInt(3, user.steals)
            }.executeUpdate()
        }
    }

    fun updateUser(user: PickpocketUser) {
        dataSource.connection.use {
            if (!it.isValid(1000)) return
            it.prepareStatement("USE pickpocket").execute()
            it.prepareStatement("UPDATE users SET steals = ? WHERE uuid = ?").also { ps ->
                ps.setInt(1, user.steals)
                ps.setString(2, user.uuid.toString())
            }.executeUpdate()
        }
    }
}