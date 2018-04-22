package io.barinek.continuum.backlog

data class StoryInfo(val id: Long, val projectId: Long, val name: String, val info: String? = null)