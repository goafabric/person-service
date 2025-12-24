package org.goafabric.personservice.extensions;

import java.util.Map;

public record EventData(String type, String operation, Object payload, Map<String, String> tenantInfos) {}
