package io.barinek.continuum.projects

data class ProjectInfoV2(val id: Long, val accountId: Long, val name: String, val active: Boolean, val funded: Boolean, val info: String? = null)