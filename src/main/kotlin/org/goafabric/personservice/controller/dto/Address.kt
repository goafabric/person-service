package org.goafabric.personservice.controller.dto

data class Address (
    val id: String? = null,
    val version: String? = null,
    val street: String,
    val city: String
)