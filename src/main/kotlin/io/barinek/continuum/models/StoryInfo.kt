package io.barinek.continuum.models

data class StoryInfo(val id: Long, val projectId: Long, val name: String, val info: String? = null)