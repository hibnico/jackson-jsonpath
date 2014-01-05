package com.fasterxml.jackson.jsonpath.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPathSingleValue;
import com.fasterxml.jackson.jsonpath.JsonPathValue;

public class JsonFieldEvaluator extends JsonPathEvaluator {

    private String fieldName;

    public JsonFieldEvaluator(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public JsonPathValue eval(JsonNode node) {
        return new JsonPathSingleValue(node.get(fieldName));
    }

}
