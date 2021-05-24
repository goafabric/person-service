package org.goafabric.personservice;

import org.goafabric.personservice.persistence.DemoDataInitializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Created by amautsch on 26.06.2015.
 */

@SpringBootApplication
        //exclude = { SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner init(ApplicationContext context, DemoDataInitializer demoDataInitializer) {
        return args -> {
            if ((args.length > 0) && ("-check-integrity".equals(args[0]))) { SpringApplication.exit(context, () -> 0);}
            else {demoDataInitializer.run();}
        };

    }
}
