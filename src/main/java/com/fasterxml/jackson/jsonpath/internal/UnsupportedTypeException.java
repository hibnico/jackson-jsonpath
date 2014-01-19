package com.fasterxml.jackson.jsonpath.internal;

import com.fasterxml.jackson.jsonpath.JsonPathRuntimeException;

public class UnsupportedTypeException extends JsonPathRuntimeException {

    UnsupportedTypeException(int position, String operator, Object... values) {
        super(buildMessage(operator, values), position);
    }

    private static String buildMessage(String operator, Object... values) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("the operator '");
        buffer.append(operator);
        buffer.append("' is undefined for the argument type(s) ");
        for (int i = 0; i < values.length; i++) {
            buffer.append(values[i].getClass().getName());
            if (i < values.length - 1) {
                buffer.append(", ");
            }
        }
        return buffer.toString();
    }
}
