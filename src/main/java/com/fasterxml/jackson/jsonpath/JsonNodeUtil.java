/*
 * Copyright 2012-2014 the original author or authors.
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
package com.fasterxml.jackson.jsonpath;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonNodeUtil {

    public static ArrayNode arrayNode(Object... values) {
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        for (Object value : values) {
            if (value == null) {
                array.addNull();
            } else if (value instanceof JsonNode) {
                array.add((JsonNode) value);
            } else if (value instanceof Integer) {
                array.add((Integer) value);
            } else if (value instanceof Double) {
                array.add((Double) value);
            } else if (value instanceof Long) {
                array.add((Long) value);
            } else if (value instanceof Float) {
                array.add((Float) value);
            } else if (value instanceof BigDecimal) {
                array.add((BigDecimal) value);
            } else if (value instanceof Boolean) {
                array.add((Boolean) value);
            } else if (value instanceof String) {
                array.add((String) value);
            } else if (value instanceof byte[]) {
                array.add((byte[]) value);
            } else {
                throw new IllegalArgumentException("unsupported value type " + value.getClass().getName());
            }
        }
        return array;
    }

    public static ObjectNode objectNode(Object... values) {
        if (values.length % 2 != 0) {
            throw new IllegalArgumentException("objectNode needs a list of pair of values");
        }
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        for (int i = 0; i < values.length; i += 2) {
            String field = values[i].toString();
            Object value = values[i + 1];
            if (value == null) {
                node.putNull(field);
            } else if (value instanceof JsonNode) {
                node.put(field, (JsonNode) value);
            } else if (value instanceof Integer) {
                node.put(field, (Integer) value);
            } else if (value instanceof Double) {
                node.put(field, (Double) value);
            } else if (value instanceof Long) {
                node.put(field, (Long) value);
            } else if (value instanceof Float) {
                node.put(field, (Float) value);
            } else if (value instanceof BigDecimal) {
                node.put(field, (BigDecimal) value);
            } else if (value instanceof Boolean) {
                node.put(field, (Boolean) value);
            } else if (value instanceof String) {
                node.put(field, (String) value);
            } else if (value instanceof byte[]) {
                node.put(field, (byte[]) value);
            } else {
                throw new IllegalArgumentException("unsupported value type " + value.getClass().getName());
            }
        }
        return node;
    }

}
