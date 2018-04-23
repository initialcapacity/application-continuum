package io.barinek.continuum.projects

data class ProjectRecord(val id: Long, val accountId: Long, val name: String, val active: Boolean, val funded: Boolean)