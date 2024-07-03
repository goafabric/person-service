package org.goafabric.personservice.controller.dto

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class Address (
    val id: String? = null,
    val version: String? = null,
    @field:Size(min = 3, max = 255) @field:Pattern(regexp = "[a-zA-Z0-9.\\s]*") val street: String,
    @field:Size(min = 3, max = 255) val city: String
)