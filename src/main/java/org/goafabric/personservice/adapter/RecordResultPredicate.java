package org.goafabric.personservice.adapter;

import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.function.Predicate;

public class RecordResultPredicate implements Predicate<Throwable> {
    @Override
    public boolean test(Throwable result) {
        return (result instanceof HttpServerErrorException
                    || result instanceof ResourceAccessException);
    }
}
