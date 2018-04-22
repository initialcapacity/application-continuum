package io.barinek.continuum.models

data class AccountInfo(val id: Long, val ownerId: Long, val name: String, val info: String? = null)