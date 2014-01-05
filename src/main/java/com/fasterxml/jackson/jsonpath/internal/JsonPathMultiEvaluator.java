package com.fasterxml.jackson.jsonpath.internal;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.jsonpath.JsonPathValue;

public abstract class JsonPathMultiEvaluator extends JsonPathEvaluator {

    public JsonPathValue eval(List<JsonNode> nodes) {
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        array.addAll(nodes);
        return eval(array);
    }

}
