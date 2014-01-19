/*
 * Copyright 2014 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fasterxml.jackson.jsonpath.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.jsonpath.JsonPathMultiValue;
import com.fasterxml.jackson.jsonpath.JsonPathNoValue;
import com.fasterxml.jackson.jsonpath.JsonPathSingleValue;
import com.fasterxml.jackson.jsonpath.JsonPathValue;

public abstract class JsonPathExpression {

    int position;

    JsonPathExpression[] children;

    JsonPathExpression(int position, JsonPathExpression... children) {
        this.position = position;
        this.children = children;
    }

    public JsonPathValue eval(JsonPathContext context) {
        JsonPathValue[] values = new JsonPathValue[children.length];
        for (int i = 0; i < children.length; i++) {
            values[i] = children[i].eval(context);
        }
        JsonNode[] nodes = new JsonNode[children.length];
        return explodedCompute(context, values, 0, nodes);
    }

    private JsonPathValue explodedCompute(JsonPathContext context, JsonPathValue[] values, int i, JsonNode[] nodes) {
        if (i == values.length) {
            return compute(context, nodes);
        }
        if (values[i] instanceof JsonPathMultiValue) {
            JsonPathMultiValue ret = new JsonPathMultiValue();
            for (JsonNode node : ((JsonPathMultiValue) values[i]).getNodes()) {
                nodes[i] = node;
                JsonPathValue value = explodedCompute(context, values, i + 1, nodes);
                value.addTo(ret);
            }
            return ret;
        }
        nodes[i] = values[i].toNode();
        return explodedCompute(context, values, i + 1, nodes);
    }

    JsonPathValue compute(JsonPathContext context, JsonNode[] childValues) {
        JsonNode node = computeNode(context, childValues);
        if (node == null || node.isNull()) {
            return JsonPathNoValue.INSTANCE;
        }
        return new JsonPathSingleValue(node);
    }

    JsonNode computeNode(JsonPathContext context, JsonNode[] childValues) {
        Object v = computeObject(context, childValues);
        if (v instanceof JsonNode) {
            return (JsonNode) v;
        }
        if (v == null) {
            return null;
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
        throw new IllegalStateException("Unsupported object " + v.getClass().getName());
    }

    Object computeObject(JsonPathContext context, JsonNode[] childValues) {
        throw new IllegalStateException("one of eval, evalNode or evalObject muste be implemented");
    }

    Number asLenientNumber(JsonNode node) {
        if (node instanceof NumericNode) {
            NumericNode n = (NumericNode) node;
            if (n.isDouble()) {
                return n.asDouble();
            }
            if (n.isLong()) {
                return n.asLong();
            }
            if (n.isInt()) {
                return n.asInt();
            }
            return null;
        }
        return null;
    }

    Number asNullableNumber(JsonNode node, Object... context) {
        if (node == null || node.isNull()) {
            return null;
        }
        Number n = asLenientNumber(node);
        if (n == null) {
            throw new TypeMismatchException(position, "number", node, context);
        }
        return n;
    }

    Number asNumber(JsonNode JsonNode, Object... context) {
        Number n = asNullableNumber(JsonNode, context);
        if (n == null) {
            throw new NullJsonException(position);
        }
        return n;
    }

    Integer asLenientInt(JsonNode node) {
        if (node instanceof NumericNode) {
            return ((NumericNode) node).asInt();
        }
        return null;
    }

    Integer asNullableInt(JsonNode node, Object... context) {
        if (node == null || node.isNull()) {
            return null;
        }
        Integer i = asLenientInt(node);
        if (i == null) {
            throw new TypeMismatchException(position, "integer", node, context);
        }
        return i;
    }

    int asInt(JsonNode node, Object... context) {
        Integer i = asNullableInt(node, context);
        if (i == null) {
            throw new NullJsonException(position);
        }
        return i;
    }

    int evalAsInt(JsonPathContext jpcontext, Object... context) {
        return asInt(eval(jpcontext).toNode(), context);
    }

    Long asLenientLong(JsonNode node) {
        if (node instanceof NumericNode) {
            return ((NumericNode) node).asLong();
        }
        return null;
    }

    Long asNullableLong(JsonNode node, Object... context) {
        if (node == null || node.isNull()) {
            return null;
        }
        Long l = asLenientLong(node);
        if (l == null) {
            throw new TypeMismatchException(position, "long", node, context);
        }
        return l;
    }

    long asLong(JsonNode node, Object... context) {
        Long l = asNullableLong(node, context);
        if (l == null) {
            throw new NullJsonException(position);
        }
        return l;
    }

    long evalAsLong(JsonPathContext jpcontext, Object... context) {
        return asLong(eval(jpcontext).toNode(), context);
    }

    Double asLenientDouble(JsonNode node) {
        if (node instanceof NumericNode) {
            return ((NumericNode) node).asDouble();
        }
        return null;
    }

    Double asNullableDouble(JsonNode node, Object... context) {
        if (node == null || node.isNull()) {
            return null;
        }
        Double d = asLenientDouble(node);
        if (d == null) {
            throw new TypeMismatchException(position, "double", node, context);
        }
        return d;
    }

    double asDouble(JsonNode node, Object... context) {
        Double d = asNullableDouble(node, context);
        if (d == null) {
            throw new NullJsonException(position);
        }
        return d;
    }

    double evalAsDouble(JsonPathContext jpcontext, Object... context) {
        return asDouble(eval(jpcontext).toNode(), context);
    }

    String asLenientString(JsonNode node) {
        if (node instanceof TextNode) {
            return ((TextNode) node).asText();
        }
        return null;
    }

    String asNullableString(JsonNode node, Object... context) {
        if (node == null || node.isNull()) {
            return null;
        }
        String s = asLenientString(node);
        if (s == null) {
            throw new TypeMismatchException(position, "string", node, context);
        }
        return s;
    }

    String asString(JsonNode node, Object... context) {
        String s = asNullableString(node, context);
        if (s == null) {
            throw new NullJsonException(position);
        }
        return s;
    }

    String evalAsString(JsonPathContext jpcontext, Object... context) {
        return asString(eval(jpcontext).toNode(), context);
    }

    Boolean asLenientBoolean(JsonNode node) {
        if (node instanceof BooleanNode) {
            return ((BooleanNode) node).asBoolean();
        }
        return null;
    }

    Boolean asNullableBoolean(JsonNode node, Object... context) {
        if (node == null || node.isNull()) {
            return null;
        }
        Boolean b = asLenientBoolean(node);
        if (b == null) {
            throw new TypeMismatchException(position, "boolean", node, context);
        }
        return b;
    }

    boolean asBoolean(JsonNode node, Object... context) {
        Boolean b = asNullableBoolean(node, context);
        if (b == null) {
            throw new NullJsonException(position);
        }
        return b;
    }

    boolean evalAsBoolean(JsonPathContext jpcontext, Object... context) {
        return asBoolean(eval(jpcontext).toNode(), context);
    }
}
