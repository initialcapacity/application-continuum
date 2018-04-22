package io.barinek.continuum.timesheets

import java.time.LocalDate

data class TimeEntryInfo(val id: Long, val projectId: Long, val userId: Long, val date: LocalDate, val hours: Int, val info: String? = null)