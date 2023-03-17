package org.goafabric.personservice;

import io.micrometer.observation.ObservationPredicate;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


/**
 * Created by amautsch on 26.06.2015.
 */

@SpringBootApplication
@RegisterReflectionForBinding(org.postgresql.util.PGobject.class)
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner init(ApplicationContext context) {
        return args -> {
            if ((args.length > 0) && ("-check-integrity".equals(args[0]))) { SpringApplication.exit(context, () -> 0);}
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, @Value("${security.authentication.enabled:true}") Boolean isAuthenticationEnabled) throws Exception {
        return isAuthenticationEnabled ? http.authorizeHttpRequests().anyRequest().authenticated().and().httpBasic().and().csrf().disable().build()
                : http.authorizeHttpRequests().anyRequest().permitAll().and().build();
    }

    @Bean
    ObservationPredicate disableHttpServerObservationsFromName() { return (name, context) -> !name.startsWith("spring.security."); }

}
