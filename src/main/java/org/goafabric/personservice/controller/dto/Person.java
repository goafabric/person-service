package org.goafabric.personservice.controller.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record Person (
    @Nullable String id,
    @Nullable Long version,
    @NotNull @Size(min = 3, max = 255) String firstName,
    @NotNull @Size(min = 3, max = 255) String lastName,
    List<Address> address) {
}
