package io.barinek.continuum.timesheets

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProjectInfo(val active: Boolean, val funded: Boolean)