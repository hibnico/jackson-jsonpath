package com.fasterxml.jackson.jsonpath.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPathMultiValue;

public class JsonWildcardEvaluator extends JsonPathEvaluator {

    @Override
    public JsonPathMultiValue eval(JsonNode node) {
        JsonPathMultiValue ret = new JsonPathMultiValue();
        if (node.isArray()) {
            for (JsonNode current : node) {
                for (JsonNode value : current) {
                    ret.add(value);
                }
            }
        } else {
            for (JsonNode subNode : node) {
                ret.add(subNode);
            }
        }
        return ret;
    }

}
