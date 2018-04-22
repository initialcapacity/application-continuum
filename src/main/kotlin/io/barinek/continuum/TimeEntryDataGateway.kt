package io.barinek.continuum

import java.time.LocalDate

class TimeEntryDataGateway(val jdbcTemplate: JdbcTemplate) {

    fun create(projectId: Long, userId: Long, date: LocalDate, hours: Int): TimeEntryRecord {
        val sql = "insert into time_entries (project_id, user_id, date, hours) values (?, ?, ?, ?)"
        return jdbcTemplate.create(sql, { id -> TimeEntryRecord(id, projectId, userId, date, hours) }, projectId, userId, date, hours)
    }

    fun findBy(userId: Long): List<TimeEntryRecord> {
        val sql = "select id, project_id, user_id, date, hours from time_entries where user_id = ?"
        return jdbcTemplate.findBy(sql, { rs -> TimeEntryRecord(rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getDate(4).toLocalDate(), rs.getInt(5)) }, userId)
    }
}