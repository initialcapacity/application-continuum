package io.barinek.continuum.models

import java.time.LocalDate

data class TimeEntryRecord(val id: Long, val projectId: Long, val userId: Long, val date: LocalDate, val hours: Int)