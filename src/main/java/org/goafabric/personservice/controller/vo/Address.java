package org.goafabric.personservice.controller.vo;

public record Address (
        String id,
        String personId,
        String street,
        String city
) {}

