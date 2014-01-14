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
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.TextNode;

public abstract class JSExpr {

    public abstract Object eval(JsonNode node);

    public Number asLenientNumber(Object v) {
        if (v instanceof Number) {
            return (Number) v;
        }
        if (v instanceof NumericNode) {
            NumericNode n = (NumericNode) v;
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

    public Number asNullableNumber(Object v) {
        if (v == null) {
            return null;
        }
        Number n = asLenientNumber(v);
        if (n == null) {
            throw new IllegalStateException(v.getClass() + " not a number");
        }
        return n;
    }

    public Number asNumber(Object v) {
        Number n = asNullableNumber(v);
        if (n == null) {
            throw new IllegalStateException("NPE");
        }
        return n;
    }

    public Integer asLenientInt(Object v) {
        if (v instanceof NumericNode) {
            return ((NumericNode) v).asInt();
        }
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }
        return null;
    }

    public Integer asNullableInt(Object v) {
        if (v == null) {
            return null;
        }
        Integer i = asLenientInt(v);
        if (i == null) {
            throw new IllegalStateException(v.getClass() + " not int");
        }
        return i;
    }

    public int asInt(Object v) {
        Integer i = asNullableInt(v);
        if (i == null) {
            throw new IllegalStateException("NPE");
        }
        return i;
    }

    public int evalAsInt(JsonNode node) {
        return asInt(eval(node));
    }

    public Long asLenientLong(Object v) {
        if (v instanceof NumericNode) {
            return ((NumericNode) v).asLong();
        }
        if (v instanceof Number) {
            return ((Number) v).longValue();
        }
        return null;
    }

    public Long asNullableLong(Object v) {
        if (v == null) {
            return null;
        }
        Long l = asLenientLong(v);
        if (l == null) {
            throw new IllegalStateException(v.getClass() + " not long");
        }
        return l;
    }

    public long asLong(Object v) {
        Long l = asNullableLong(v);
        if (l == null) {
            throw new IllegalStateException("NPE");
        }
        return l;
    }

    public long evalAsLong(JsonNode node) {
        return asLong(eval(node));
    }

    public Double asLenientDouble(Object v) {
        if (v instanceof NumericNode) {
            return ((NumericNode) v).asDouble();
        }
        if (v instanceof Number) {
            return ((Number) v).doubleValue();
        }
        return null;
    }

    public Double asNullableDouble(Object v) {
        if (v == null) {
            return null;
        }
        Double d = asLenientDouble(v);
        if (d == null) {
            throw new IllegalStateException(v.getClass() + " not double");
        }
        return d;
    }

    public double asDouble(Object v) {
        Double d = asNullableDouble(v);
        if (d == null) {
            throw new IllegalStateException("NPE");
        }
        return d;
    }

    public double evalAsDouble(JsonNode node) {
        return asDouble(eval(node));
    }

    public String asLenientString(Object v) {
        if (v instanceof String) {
            return (String) v;
        }
        if (v instanceof TextNode) {
            return ((TextNode) v).asText();
        }
        return null;
    }

    public String asNullableString(Object v) {
        if (v == null) {
            return null;
        }
        String s = asLenientString(v);
        if (s == null) {
            throw new IllegalStateException(v.getClass() + " not string");
        }
        return s;
    }

    public String asString(Object v) {
        String s = asNullableString(v);
        if (s == null) {
            throw new IllegalStateException("NPE");
        }
        return s;
    }

    public String evalAsString(JsonNode node) {
        return asString(eval(node));
    }

    public Boolean asLenientBoolean(Object v) {
        if (v instanceof Boolean) {
            return (Boolean) v;
        }
        if (v instanceof BooleanNode) {
            return ((BooleanNode) v).asBoolean();
        }
        return null;
    }

    public Boolean asNullableBoolean(Object v) {
        if (v == null) {
            return null;
        }
        Boolean b = asLenientBoolean(v);
        if (b == null) {
            throw new IllegalStateException(v.getClass() + " not boolean");
        }
        return b;
    }

    public boolean asBoolean(Object v) {
        Boolean b = asNullableBoolean(v);
        if (b == null) {
            throw new IllegalStateException("NPE");
        }
        return b;
    }

    public boolean evalAsBoolean(JsonNode node) {
        return asBoolean(eval(node));
    }

}
