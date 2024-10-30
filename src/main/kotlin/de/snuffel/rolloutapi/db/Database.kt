package de.snuffel.rolloutapi.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.Database as ExposedDatabase

object Rollouts : Table() {
    val id = varchar("id", 50).primaryKey()
    val rollout = blob("rollout")
}

object Database {
    private lateinit var db: ExposedDatabase

    fun init() {
        db = ExposedDatabase.connect("jdbc:sqlite:rollout.db", driver = "org.sqlite.JDBC")
        transaction(db) {
            create(Rollouts)
        }
    }

    fun insertRollout(id: String, rollout: ByteArray) {
        transaction(db) {
            Rollouts.insert {
                it[Rollouts.id] = id
                it[Rollouts.rollout] = ExposedBlob(rollout)
            }
        }
    }

    fun getRolloutById(id: String): ByteArray? {
        return transaction(db) {
            Rollouts.select { Rollouts.id eq id }
                .map { it[Rollouts.rollout].bytes }
                .singleOrNull()
        }
    }

    fun getRolloutsByDateRange(startDate: String, endDate: String): List<ByteArray> {
        // Implement the logic to query rollouts by date range
        return emptyList()
    }
}
