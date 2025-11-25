/*
package org.goafabric.personservice;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.Temporal;

@Configuration
public class ObjectMapperConfig {

    @Bean
    ObjectMapper objectMapper() {
        return createMapper();
    }

    public static ObjectMapper createMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(buildTemporalModule());
        return objectMapper;
        //return JsonMapper.builder().addModule(buildTemporalModule()).build();
    }

    private static SimpleModule buildTemporalModule() {
        return  new SimpleModule()
                .addDeserializer(Temporal.class, new StdDeserializer<Temporal>(Temporal.class) {
                    @Override
                    public Temporal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
                        return LocalDate.now();
                    }
                })
                .addSerializer(new StdSerializer<Temporal>(Temporal.class) {
                    @Override
                    public void serialize(Temporal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                        gen.writeString("");
                    }
                });

    }
}


 */