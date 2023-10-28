package org.goafabric.personservice.controller.vo

data class Person (
    val id:  String? = null,
    val version:  String? = null,
    val firstName: String,
    val lastName: String,
    val address: List<Address?>
)