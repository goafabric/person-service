package org.goafabric.personservice.controller.vo

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Null
import jakarta.validation.constraints.Size
import org.goafabric.personservice.controller.vo.Address


data class Person (
    val id: @Null String? = null,
    val firstName: @NotNull @Size(min = 3, max = 255) String,
    val lastName: @NotNull @Size(min = 3, max = 255) String,
    val address: Address?
)