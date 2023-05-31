package org.goafabric.personservice.persistence.extensions;

import org.goafabric.personservice.controller.dto.Address;
import org.goafabric.personservice.controller.dto.Person;
import org.goafabric.personservice.crossfunctional.HttpInterceptor;
import org.goafabric.personservice.logic.PersonLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class DatabaseProvisioning implements CommandLineRunner {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${database.provisioning.goals:}")
    String goals;

    @Autowired
    PersonLogic personLogic;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
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
        HttpInterceptor.setTenantId("0");
        if (personLogic.findAll().isEmpty()) {
            HttpInterceptor.setTenantId("0");
            insertData();
            HttpInterceptor.setTenantId("5a2f");
            insertData();
        }
    }

    private void insertData() {
        personLogic.save(new Person(null, "Homer", "Simpson"
                        , createAddress("Evergreen Terrace 1")));

        personLogic.save(new Person(null, "Bart", "Simpson"
                , createAddress("Everblue Terrace 1")));

        personLogic.save(new Person(null, "Monty", "Burns"
                , createAddress("Monty Mansion")));

    }

    private Address createAddress(String street) {
        return new Address(null, "street", "Springfield " + HttpInterceptor.getTenantId());
    }

}