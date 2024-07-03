package org.goafabric.personservice.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

public record Address (
        @Null String id,
        @Null Long version,
        @NotNull @Size(min = 3, max = 255) String street,
        @NotNull @Size(min = 3, max = 255) String city) {
}

