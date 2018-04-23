package io.barinek.continuum.projects

data class ProjectInfoV1(val id: Long, val accountId: Long, val name: String, val active: Boolean, val info: String? = null)