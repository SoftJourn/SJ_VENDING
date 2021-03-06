package com.softjourn.vending.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.data.domain.Sort;

import java.io.IOException;

public class SortJsonDeserializer extends JsonDeserializer<Sort> {

    @Override
    public Sort deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ArrayNode node = jp.getCodec().readTree(jp);
        Sort.Order[] orders = new Sort.Order[node.size()];
        int i = 0;
        for(JsonNode obj : node){
            orders[i] =  new Sort.Order(Sort.Direction.valueOf(obj.get("direction").asText()), obj.get("property").asText());
            i++;
        }
        if (orders.length == 0) {
            return null;
        }
        return new Sort(orders);
    }

}
