package io.barinek.continuum

class StoryDataGateway(val jdbcTemplate: JdbcTemplate) {

    fun create(projectId: Long, name: String): StoryRecord {
        val sql = "insert into stories (project_id, name) values (?, ?)"
        return jdbcTemplate.create(sql, { id -> StoryRecord(id, projectId, name) }, projectId, name)
    }

    fun findBy(projectId: Long): List<StoryRecord> {
        val sql = "select id, project_id, name from stories where project_id = ?"
        return jdbcTemplate.findBy(sql, { rs -> StoryRecord(rs.getLong(1), rs.getLong(2), rs.getString(3)) }, projectId)
    }
}