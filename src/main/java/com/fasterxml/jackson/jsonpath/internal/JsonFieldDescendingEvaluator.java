package com.fasterxml.jackson.jsonpath.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPathMultiValue;
import com.fasterxml.jackson.jsonpath.JsonPathValue;

public class JsonFieldDescendingEvaluator extends JsonPathEvaluator {

    @Override
    public JsonPathValue eval(JsonNode node) {
        JsonPathMultiValue ret = new JsonPathMultiValue();
        descend(node, ret);
        return ret;
    }

    private void descend(JsonNode node, JsonPathMultiValue result) {
        if (node.isObject()) {
            result.add(node);
            for (JsonNode value : node) {
                if (value.isContainerNode()) {
                    descend(value, result);
                }
            }
        } else if (node.isArray()) {
            for (JsonNode value : node) {
                if (value.isContainerNode()) {
                    descend(value, result);
                }
            }
        }
    }
}
