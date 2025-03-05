package org.goafabric.personservice.v1.controller.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.goafabric.personservice.base.controller.dto.Address;

import java.util.List;

public record Person (
    @Nullable String id,
    @Nullable Long version,
    @NotNull @Size(min = 3, max = 255) String firstName,
    @NotNull @Size(min = 3, max = 255) String lastName,
    @NotNull @Size(min = 3, max = 255) String middleName,
    List<Address> address) {
}
