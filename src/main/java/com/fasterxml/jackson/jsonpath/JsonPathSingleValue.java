package com.fasterxml.jackson.jsonpath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.internal.JsonPathEvaluator;

public class JsonPathSingleValue extends JsonPathValue {

    private JsonNode node;

    public JsonPathSingleValue(JsonNode node) {
        this.node = node;
    }

    @Override
    public JsonPathValue apply(JsonPathEvaluator evaluator) {
        return evaluator.eval(node);
    }

    @Override
    public void addTo(JsonPathMultiValue ret) {
        ret.add(node);
    }

    @Override
    public JsonNode toNode() {
        return node;
    }
}
