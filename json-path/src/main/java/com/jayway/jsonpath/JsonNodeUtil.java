package com.jayway.jsonpath;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;

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
            return mapper.readValue(node, Object.class);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
