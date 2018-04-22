package io.barinek.continuum.users

import io.barinek.continuum.jdbcsupport.JdbcTemplate
import java.sql.Connection

class UserDataGateway(val jdbcTemplate: JdbcTemplate) {
    val createSql = "insert into users (name) values (?)"

    fun create(connection: Connection, name: String): UserRecord {
        return jdbcTemplate.create(connection, createSql, { id -> UserRecord(id, name) }, name)
    }

    fun create(name: String): UserRecord {
        return jdbcTemplate.create(createSql, { id -> UserRecord(id, name) }, name)
    }

    fun findObjectBy(id: Long): UserRecord {
        val s = "select id, name from users where id = ? limit 1"
        val find = jdbcTemplate.findBy(s, { rs -> UserRecord(rs.getLong(1), rs.getString(2)) }, id)
        return find[0]
    }
}