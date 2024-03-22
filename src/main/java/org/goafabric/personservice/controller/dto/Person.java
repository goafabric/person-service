package org.goafabric.personservice.controller.dto;

import java.util.List;
import java.util.Objects;

public record Person (
    String id,
    Long version,
    String firstName,
    String lastName,
    List<Address> address) {
    public Person {
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);
    }
}
