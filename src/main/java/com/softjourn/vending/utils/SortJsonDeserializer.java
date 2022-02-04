package com.softjourn.vending.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;

public class SortJsonDeserializer extends JsonDeserializer<Sort> {

  @Override
  public Sort deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    ContainerNode<?> cNode = jp.getCodec().readTree(jp);
    List<Sort.Order> orders = new ArrayList<>();
    if (cNode instanceof ArrayNode) {
      ArrayNode node = (ArrayNode) cNode;
      for (JsonNode obj : node) {
        orders.add(new Sort.Order(
            Sort.Direction.valueOf(obj.get("direction").asText()), obj.get("property").asText()));
      }
    }
    if (orders.isEmpty()) {
      return null;
    }
    return Sort.by(orders);
  }
}
