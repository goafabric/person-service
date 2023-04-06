package org.goafabric.personservice.persistence;


import org.goafabric.personservice.controller.dto.Address;
import org.goafabric.personservice.controller.dto.Person;
import org.goafabric.personservice.crossfunctional.HttpInterceptor;
import org.goafabric.personservice.logic.PersonLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
public class DatabaseProvisioning {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String goals;

    private final PersonLogic personLogic;

    private final ApplicationContext applicationContext;

    public DatabaseProvisioning(@Value("${database.provisioning.goals:}") String goals, PersonLogic personLogic, ApplicationContext applicationContext) {
        this.goals = goals;
        this.personLogic = personLogic;
        this.applicationContext = applicationContext;
    }

    @Bean
    public CommandLineRunner commandLineRunnerProv() {
        return args -> this.run();
    }
    
    public void run() {
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
        boolean dataExists = false;
        try {
            dataExists = personLogic.findAll().iterator().hasNext();
        }
        catch (DataAccessException e) {} //happens on first elastic run

        if (!dataExists) {
            createDemoData("0");
            createDemoData("5a2f");
        } else {
            log.info("Demo data already exists, skipping import ...");
        }
    }

    private void createDemoData(String tenantId) {
        HttpInterceptor.setTenantId(tenantId);
        insertData();
    }

    private void insertData() {
        personLogic.save(new Person(null,
                "Homer",
                "Simpson",
                createAddress("Evergreen Terrace 1")));

        personLogic.save(new Person(null,
                "Bart",
                "Simpson",
                createAddress("Everblue Terrace 1")));

        personLogic.save(new Person(null,
                "Monty",
                "Burns",
                createAddress("Monty Mansion")));
    }

    private Address createAddress(String street) {
        return new Address(null,
                street, "Springfield " + HttpInterceptor.getTenantId());
    }


}