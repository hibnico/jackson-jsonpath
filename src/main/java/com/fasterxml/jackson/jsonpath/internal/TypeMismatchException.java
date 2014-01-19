package com.fasterxml.jackson.jsonpath.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPathRuntimeException;

public class TypeMismatchException extends JsonPathRuntimeException {

    TypeMismatchException(int position, String expected, JsonNode value, Object... context) {
        super(buildMessage(expected, value, context), position);
    }

    private static String buildMessage(String expected, JsonNode value, Object... context) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Expecting a ");
        buffer.append(expected);
        buffer.append(" but was a ");
        buffer.append(value.getNodeType().toString().toLowerCase());
        buffer.append(" in ");
        for (Object c : context) {
            buffer.append(c);
        }
        return buffer.toString();
    }
}
