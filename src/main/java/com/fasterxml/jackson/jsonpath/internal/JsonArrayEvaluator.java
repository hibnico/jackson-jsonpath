package com.fasterxml.jackson.jsonpath.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPathSingleValue;

public abstract class JsonArrayEvaluator extends JsonPathMultiEvaluator {

    public abstract int getIndex(JsonNode node);
    
    @Override
    public JsonPathSingleValue eval(JsonNode node) {
        if (node.isArray()) {
            throw new IllegalStateException("node is not an array");
        }
        int index = getIndex(node);
        return new JsonPathSingleValue(node.get(index));
    }

}
