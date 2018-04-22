package io.barinek.continuum.accounts

data class AccountInfo(val id: Long, val ownerId: Long, val name: String, val info: String? = null)