package org.goafabric.personservice;

import org.hibernate.annotations.SQLInsert;
import org.hibernate.boot.model.relational.ColumnOrderingStrategyStandard;
import org.hibernate.boot.models.annotations.internal.CacheAnnotation;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.annotation.RegisterReflection;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
@RegisterReflection(classes = {ColumnOrderingStrategyStandard.class, CacheAnnotation.class, SQLInsert.class}, memberCategories = {MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.ACCESS_DECLARED_FIELDS})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner init(ConfigurableApplicationContext context) {
        return args -> {
            if ((args.length > 0) && ("-check-integrity".equals(args[0]))) {
                context.addApplicationListener((ApplicationListener<ApplicationReadyEvent>) event -> {
                    RestClient.create().get().uri("http://localhost:" + context.getEnvironment().getProperty("local.server.port") + "/v3/api-docs").retrieve().body(String.class);
                    SpringApplication.exit(context, () -> 0);
                });
            }
        };
    }

}
