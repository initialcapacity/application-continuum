package io.barinek.continuum.projects

import io.barinek.continuum.jdbcsupport.JdbcTemplate

class ProjectDataGateway(val jdbcTemplate: JdbcTemplate) {

    fun create(accountId: Long, name: String): ProjectRecord {
        val sql = "insert into projects (account_id, name) values (?, ?)"
        return jdbcTemplate.create(sql, { id -> ProjectRecord(id, accountId, name) }, accountId, name)
    }

    fun findBy(accountId: Long): List<ProjectRecord> {
        val sql = "select id, account_id, name from projects where account_id = ? order by name asc"
        return jdbcTemplate.findBy(sql, { rs -> ProjectRecord(rs.getLong(1), rs.getLong(2), rs.getString(3)) }, accountId)
    }
}