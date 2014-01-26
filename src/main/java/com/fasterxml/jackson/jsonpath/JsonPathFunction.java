package com.fasterxml.jackson.jsonpath;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.jsonpath.internal.JsonPathContext;

public abstract class JsonPathFunction {

    public boolean isVector() {
        return false;
    }

    public JsonPathValue call(JsonPathContext context, List<JsonNode> args) {
        JsonNode node = callAsNode(context, args);
        if (node == null || node.isMissingNode()) {
            return JsonPathNoValue.INSTANCE;
        }
        return new JsonPathSingleValue(node);
    }

    protected JsonNode callAsNode(JsonPathContext context, List<JsonNode> args) {
        Object v = callAsObject(context, args);
        if (v instanceof JsonNode) {
            return (JsonNode) v;
        }
        if (v == null) {
            return JsonNodeFactory.instance.nullNode();
        }
        if (v instanceof String) {
            return JsonNodeFactory.instance.textNode((String) v);
        }
        if (v instanceof Integer) {
            return JsonNodeFactory.instance.numberNode((Integer) v);
        }
        if (v instanceof Long) {
            return JsonNodeFactory.instance.numberNode((Long) v);
        }
        if (v instanceof Double) {
            return JsonNodeFactory.instance.numberNode((Double) v);
        }
        if (v instanceof Boolean) {
            return JsonNodeFactory.instance.booleanNode((Boolean) v);
        }
        if (v instanceof Character) {
            return JsonNodeFactory.instance.textNode(new String(new char[] { (Character) v }));
        }
        throw new IllegalStateException("Unsupported object " + v.getClass().getName());
    }

    protected Object callAsObject(JsonPathContext context, List<JsonNode> args) {
        throw new IllegalStateException(
                "one of call, callAsNode or callAsObject must be implemented in function implementation "
                        + getClass().getName());
    }

}
