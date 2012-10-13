/*
 * Copyright 2012 the original author or authors.
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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class JsonNodeUtil {

    public static ArrayNode asArrayNode(Object... values) {
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

    public static List< ? > asList(JsonNode node) {
        return (List< ? >) asJava(node);
    }

    public static Map< ? , ? > asMap(JsonNode node) {
        return (Map< ? , ? >) asJava(node);
    }

    public static Object asJava(JsonNode node) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.treeToValue(node, Object.class);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
