package org.goafabric.event;


import java.util.Map;

public record EventData(
    String type,
    String operation, //CREATE, UPDATE, DELETE
    Object payload,
    Map<String, String> tenantInfos
) {}