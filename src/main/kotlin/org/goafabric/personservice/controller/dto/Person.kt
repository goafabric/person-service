package org.goafabric.personservice.controller.dto

import jakarta.validation.constraints.Size

data class Person (
    val id:  String? = null,
    val version:  String? = null,
    @field:Size(min = 3, max = 255) val firstName: String,
    @field:Size(min = 3, max = 255) val lastName: String,
    val address: List<Address?>
)