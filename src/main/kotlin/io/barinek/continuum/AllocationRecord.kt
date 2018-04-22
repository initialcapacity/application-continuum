package io.barinek.continuum

import java.time.LocalDate

data class AllocationRecord(val id: Long, val projectId: Long, val userId: Long, val firstDay: LocalDate, val lastDay: LocalDate)