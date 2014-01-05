package com.fasterxml.jackson.jsonpath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.jsonpath.internal.JsonPathEvaluator;

public class JsonPathNoValue extends JsonPathValue {

    public static final JsonPathNoValue INSTANCE = new JsonPathNoValue();

    @Override
    public JsonPathValue apply(JsonPathEvaluator evaluator) {
        return INSTANCE;
    }

    @Override
    public void addTo(JsonPathMultiValue ret) {
        // nothing to do
    }

    @Override
    public JsonNode toNode() {
        return JsonNodeFactory.instance.nullNode();
    }
}
