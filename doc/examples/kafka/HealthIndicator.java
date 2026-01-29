//public class KafkaInterceptor implements HealthIndicator

@Override
public Health health() {
    try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
        client.describeCluster().nodes().get(1, TimeUnit.SECONDS);
        return Health.up().build();
    } catch (Exception e) {
        return Health.down(e).build();
    }
}