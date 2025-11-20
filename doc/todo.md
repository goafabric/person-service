# rewrite.yaml
- java 25 recipe

# code changes  + rewrite change
- Jackson Import Changes
- reflection hint changes Flyway, Type, Kafka

# bug create (verify with snapshot)
- Kotlin 2.3.0 + kapt + mapstruct

# reverify
- springdoc version upgrade after 4.0 GA : https://github.com/springdoc/springdoc-openapi/releases
- net.ttddyy upgrade after 4.0 GA : https://github.com/jdbc-observations/datasource-micrometer/releases
- 
- JsonIT does not work with jackson-boot2 autoconfig : https://github.com/spring-projects/spring-boot/issues/48198
