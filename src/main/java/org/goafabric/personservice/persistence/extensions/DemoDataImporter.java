package org.goafabric.personservice.persistence.extensions;

import org.goafabric.personservice.controller.dto.Address;
import org.goafabric.personservice.controller.dto.Person;
import org.goafabric.personservice.extensions.TenantContext;
import org.goafabric.personservice.logic.PersonLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class DemoDataImporter implements CommandLineRunner {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String goals;

    private final String tenants;

    private final ApplicationContext applicationContext;

    public DemoDataImporter(@Value("${database.provisioning.goals:}")String goals, @Value("${multi-tenancy.tenants}") String tenants,
                            ApplicationContext applicationContext) {
        this.goals = goals;
        this.tenants = tenants;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) {
        if ((args.length > 0) && ("-check-integrity".equals(args[0]))) { return; }

        if (goals.contains("-import-demo-data")) {
            log.info("Importing demo data ...");
            importDemoData();
            log.info("Demo data import done ...");
        }

        if (goals.contains("-terminate")) {
            log.info("Terminating app ...");
            SpringApplication.exit(applicationContext, () -> 0); //if an exception is raised, spring will automatically terminate with 1
        }
    }

    private void importDemoData() {
        Arrays.asList(tenants.split(",")).forEach(tenant -> {
            TenantContext.setTenantId(tenant);
            if (applicationContext.getBean(PersonLogic.class).findAll().isEmpty()) {
                insertData();
            }
        });
        TenantContext.setTenantId("0");
    }

    private void insertData() {
        IntStream.range(0, 1).forEach(i -> {
            applicationContext.getBean(PersonLogic.class).save(new Person(null, null, "Homer", "Simpson"
                    , List.of(createAddress("Evergreen Terrace No. " + i))));

            applicationContext.getBean(PersonLogic.class).save(new Person(null, null, "Bart", "Simpson"
                    , List.of(createAddress("Everblue Terrace No. " + i))));

            applicationContext.getBean(PersonLogic.class).save(new Person(null, null, "Monty", "Burns"
                    , List.of(createAddress("Mammon Street No. 1000 on the corner of Croesus"))));
        });

    }

    private Address createAddress(String street) {
        return new Address(null, null, street, "Springfield " + TenantContext.getTenantId());
    }

}
