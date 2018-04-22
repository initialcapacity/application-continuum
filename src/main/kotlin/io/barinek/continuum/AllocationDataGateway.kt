package io.barinek.continuum

import java.time.LocalDate

class AllocationDataGateway(val jdbcTemplate: JdbcTemplate) {

    fun create(projectId: Long, userId: Long, firstDay: LocalDate, lastDay: LocalDate): AllocationRecord {
        val sql = "insert into allocations (project_id, user_id, first_day, last_day) values (?, ?, ?, ?)"
        return jdbcTemplate.create(sql, { id -> AllocationRecord(id, projectId, userId, firstDay, lastDay) }, projectId, userId, firstDay, lastDay)
    }

    fun findBy(projectId: Long): List<AllocationRecord> {
        val sql = "select id, project_id, user_id, first_day, last_day from allocations where project_id = ? order by first_day"
        return jdbcTemplate.findBy(sql, { rs -> AllocationRecord(rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getDate(4).toLocalDate(), rs.getDate(5).toLocalDate()) }, projectId)
    }
}