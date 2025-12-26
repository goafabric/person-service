package org.goafabric.personservice.controller.dto

data class EventData(val type: String, val operation: String, val payload: Any, val tenantInfos: Map<String, String>) {
}