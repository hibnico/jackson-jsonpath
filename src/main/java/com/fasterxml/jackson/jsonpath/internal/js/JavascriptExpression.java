package com.fasterxml.jackson.jsonpath.internal.js;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class JavascriptExpression {

    public abstract Object eval(JsonNode node);

    public Integer evalNullableInt(JsonNode node) {
        Object v = eval(node);
        if (v == null) {
            return null;
        }
        if (v instanceof String) {
            return Integer.parseInt((String) v);
        }
        if (!(v instanceof Number)) {
            throw new IllegalStateException(v.getClass() + " not int");
        }
        return ((Number) v).intValue();
    }

    public int evalInt(JsonNode node) {
        Integer i = evalNullableInt(node);
        if (i == null) {
            throw new IllegalStateException("null int");
        }
        return i;
    }

    public int evalInt(JsonNode node, int def) {
        Integer i = evalNullableInt(node);
        if (i == null) {
            return def;
        }
        return i;
    }

    public Long evalNullableLong(JsonNode node) {
        Object v = eval(node);
        if (v == null) {
            return null;
        }
        if (v instanceof String) {
            return Long.parseLong((String) v);
        }
        if (!(v instanceof Number)) {
            throw new IllegalStateException(v.getClass() + " not long");
        }
        return ((Number) v).longValue();
    }

    public long evalLong(JsonNode node) {
        Long l = evalNullableLong(node);
        if (l == null) {
            throw new IllegalStateException("null long");
        }
        return l;
    }

    public long evalLong(JsonNode node, long def) {
        Long l = evalNullableLong(node);
        if (l == null) {
            return def;
        }
        return l;
    }

    public String evalNullableString(JsonNode node) {
        Object v = eval(node);
        if (v == null) {
            return null;
        }
        if (!(v instanceof String)) {
            return v.toString();
        }
        return (String) v;
    }

    public String evalString(JsonNode node) {
        String s = evalNullableString(node);
        if (s == null) {
            throw new IllegalStateException("null string");
        }
        return s;
    }

    public String evalString(JsonNode node, String def) {
        String s = evalNullableString(node);
        if (s == null) {
            return def;
        }
        return s;
    }

    public Boolean evalNullableBoolean(JsonNode node) {
        Object v = eval(node);
        if (v == null) {
            return null;
        }
        if (v instanceof String) {
            return Boolean.parseBoolean((String) v);
        }
        if (!(v instanceof Boolean)) {
            throw new IllegalStateException(v.getClass() + " not boolean");
        }
        return (Boolean) v;
    }

    public boolean evalBoolean(JsonNode node) {
        Boolean b = evalNullableBoolean(node);
        if (b == null) {
            throw new IllegalStateException("null boolean");
        }
        return (Boolean) b;
    }

    public boolean evalBoolean(JsonNode node, boolean def) {
        Boolean b = evalNullableBoolean(node);
        if (b == null) {
            return def;
        }
        return (Boolean) b;
    }
}
