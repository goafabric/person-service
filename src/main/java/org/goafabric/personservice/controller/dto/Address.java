package org.goafabric.personservice.controller.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record Address (
    @Nullable String id,
    @Nullable Long version,
    @NotNull @Size(min = 3, max = 255) @Pattern(regexp = "[a-zA-Z0-9.\\s]*") String street,
    @NotNull @Size(min = 3, max = 255) String city) {
}

