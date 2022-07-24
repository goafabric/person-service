package org.goafabric.personservice.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    //@Null
    private String id;
    
    //@NotNull
    //@Size(min = 3, max = 255)
    private String firstName;

    //@NotNull
    //@Size(min = 3, max = 255)
    private String lastName;

    private Address address;
}
