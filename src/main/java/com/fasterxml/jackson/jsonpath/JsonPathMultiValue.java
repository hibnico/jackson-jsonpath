package com.fasterxml.jackson.jsonpath;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.jsonpath.internal.JsonPathEvaluator;
import com.fasterxml.jackson.jsonpath.internal.JsonPathMultiEvaluator;

public class JsonPathMultiValue extends JsonPathValue {

    private List<JsonNode> nodes = new ArrayList<JsonNode>();

    @Override
    public JsonPathValue apply(JsonPathEvaluator evaluator) {
        if (evaluator instanceof JsonPathMultiEvaluator) {
            return ((JsonPathMultiEvaluator) evaluator).eval(nodes);
        }
        JsonPathMultiValue ret = new JsonPathMultiValue();
        for (JsonNode node : nodes) {
            JsonPathValue value = evaluator.eval(node);
            value.addTo(ret);
        }
        return ret;
    }

    @Override
    public void addTo(JsonPathMultiValue ret) {
        for (JsonNode node : nodes) {
            ret.add(node);
        }
    }

    public void add(JsonNode node) {
        nodes.add(node);
    }

    @Override
    public ArrayNode toNode() {
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        array.addAll(array);
        return array;
    }
}
