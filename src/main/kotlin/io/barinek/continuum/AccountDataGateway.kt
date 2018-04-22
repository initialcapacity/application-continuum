package io.barinek.continuum

import java.sql.Connection

class AccountDataGateway(val jdbcTemplate: JdbcTemplate) {
    val createSql = "insert into accounts (owner_id, name) values (?, ?)"

    fun create(ownerId: Long, name: String): AccountRecord {
        return jdbcTemplate.create(createSql, { id -> AccountRecord(id, ownerId, name) }, ownerId, name)
    }

    fun create(connection: Connection, ownerId: Long, name: String): AccountRecord {
        return jdbcTemplate.create(connection, createSql, { id -> AccountRecord(id, ownerId, name) }, ownerId, name)
    }

    fun findBy(ownerId: Long): List<AccountRecord> {
        val sql = "select id, owner_id, name from accounts where owner_id = ? order by name desc limit 1"
        return jdbcTemplate.findBy(sql, { rs -> AccountRecord(rs.getLong(1), rs.getLong(2), rs.getString(3)) }, ownerId)
    }
}