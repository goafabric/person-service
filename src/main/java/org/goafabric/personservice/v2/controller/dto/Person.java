package org.goafabric.personservice.v2.controller.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.goafabric.personservice.base.controller.dto.Address;

import java.util.List;

public record Person (
    @Nullable String id,
    @Nullable Long version,
    @NotNull @Size(min = 3, max = 256) String givenName,
    @NotNull @Size(min = 3, max = 256) String familyName,
    List<Address> address) {
}
