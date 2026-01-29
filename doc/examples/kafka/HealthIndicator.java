//public class KafkaInterceptor implements HealthIndicator

@Override
public Health health() {
    var props = new HashMap<>(kafkaAdmin.getConfigurationProperties());
    props.put("default.api.timeout.ms", "3000");
    try (AdminClient client = AdminClient.create(props)) {
        client.describeCluster().clusterId().get();
        return Health.up().build();
    } catch (Exception e) {
        return Health.down(e).build();
    }
}