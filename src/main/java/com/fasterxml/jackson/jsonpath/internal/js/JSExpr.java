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
package com.fasterxml.jackson.jsonpath.internal.js;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class JSExpr {

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
