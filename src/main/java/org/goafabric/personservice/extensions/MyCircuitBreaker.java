
package org.goafabric.personservice.extensions;

import java.lang.annotation.*;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface MyCircuitBreaker {


    String name();

    String fallbackMethod() default "";
}