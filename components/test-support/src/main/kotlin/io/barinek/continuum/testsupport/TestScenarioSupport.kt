package io.barinek.continuum.testsupport

import io.barinek.continuum.jdbcsupport.JdbcTemplate

class TestScenarioSupport {
    val dataSource = TestDataSourceConfig().dataSource
    val template = JdbcTemplate(dataSource)

    fun loadTestScenario(name: String) {
        this.javaClass.classLoader.getResourceAsStream(name + ".sql").reader().readLines()
                .asSequence()
                .filterNot(String::isNullOrBlank)
                .forEach { template.execute(it) }
    }
}