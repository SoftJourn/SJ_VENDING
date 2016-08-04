package com.softjourn.vending.utils;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.io.IOException;

public class IdDeserializer<T> extends JsonDeserializer<T> {

    private final CrudRepository<T, Integer> repository;

    @Autowired
    public IdDeserializer(CrudRepository<T, Integer> repository) {
        this.repository = repository;
    }

    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return null;
    }
}
