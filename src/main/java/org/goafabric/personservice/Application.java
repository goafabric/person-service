package org.goafabric.personservice;

import org.goafabric.personservice.adapter.Callee;
import org.goafabric.personservice.persistence.DatabaseProvisioning;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.Arrays;


/**
 * Created by amautsch on 26.06.2015.
 */

@SpringBootApplication
@ImportRuntimeHints(Application.ApplicationRuntimeHints.class)
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner init(ApplicationContext context, DatabaseProvisioning databaseProvisioning) {
        return args -> {
            databaseProvisioning.run();
            if ((args.length > 0) && ("-check-integrity".equals(args[0]))) { SpringApplication.exit(context, () -> 0);}
        };

    }

    static class ApplicationRuntimeHints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            Arrays.stream(Callee.class.getConstructors()).forEach(
                    r -> hints.reflection().registerConstructor(r, ExecutableMode.INVOKE));
            Arrays.stream(Callee.class.getDeclaredMethods()).forEach(
                    r -> hints.reflection().registerMethod(r, ExecutableMode.INVOKE));

            Arrays.stream(SimpleClientHttpRequestFactory.class.getConstructors()).forEach(
                    r -> hints.reflection().registerConstructor(r, ExecutableMode.INVOKE));
            Arrays.stream(SimpleClientHttpRequestFactory.class.getMethods()).forEach(
                    r -> hints.reflection().registerMethod(r, ExecutableMode.INVOKE));
        }

    }

}
