package org.goafabric.personservice;


import org.goafabric.personservice.persistence.entity.PersonEo;
import org.javers.core.Javers;
import org.javers.repository.jql.QueryBuilder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class JaversIT {
    @Autowired
    private Javers javers;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void test() {
        QueryBuilder jqlQuery = QueryBuilder.byClass(PersonEo.class);
        var snapshots = javers.findChanges(jqlQuery.build());
        assertThat(snapshots).isNotEmpty();
        snapshots.forEach(snapshot -> log.info(javers.getJsonConverter().toJson(snapshot)));
    }
}
