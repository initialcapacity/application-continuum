package io.barinek.continuum.models

data class ProjectInfo(val id: Long, val accountId: Long, val name: String, val info: String? = null)