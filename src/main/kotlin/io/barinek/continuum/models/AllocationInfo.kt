package io.barinek.continuum.models

import java.time.LocalDate

data class AllocationInfo(val id: Long, val projectId: Long, val userId: Long, val firstDay: LocalDate, val lastDay: LocalDate, val info: String? = null)