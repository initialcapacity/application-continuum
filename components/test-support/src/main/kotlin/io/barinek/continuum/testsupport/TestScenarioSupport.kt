package io.barinek.continuum.testsupport

import io.barinek.continuum.jdbcsupport.JdbcTemplate
import javax.sql.DataSource

class TestScenarioSupport(val dataSource: DataSource) {
    val template = JdbcTemplate(dataSource)

    fun loadTestScenario(name: String) {
        this.javaClass.classLoader.getResourceAsStream(name + ".sql").reader().readLines()
                .asSequence()
                .filterNot(String::isNullOrBlank)
                .forEach { template.execute(it) }
    }
}