package org.goafabric.personservice.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

import java.util.List;

public record Person (
    @Null String id,
    @Null Long version,
    @NotNull @Size(min = 3, max = 255) String firstName,
    @NotNull @Size(min = 3, max = 255) String lastName,
    List<Address> address) {
}
