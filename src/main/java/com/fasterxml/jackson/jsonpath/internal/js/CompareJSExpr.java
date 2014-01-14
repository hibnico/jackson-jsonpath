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

public class CompareJSExpr extends JSExpr {

    enum CompareOp {
        EQ("=="), NE("!="), LT("<"), GT(">"), LE("<="), GE(">=");

        private String sign;

        private CompareOp(String sign) {
            this.sign = sign;
        }
    }

    private JSExpr left;

    private JSExpr right;

    private CompareOp op;

    public CompareJSExpr(CompareOp op, JSExpr left, JSExpr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public Object eval(JsonNode node) {
        Object v1 = left.eval(node);
        Object v2 = right.eval(node);
        if (v1 == null || v2 == null) {
            throw new IllegalStateException("NPE");
        }
        switch (op) {
        case EQ:
            return v1.equals(v2);
        case NE:
            return !v1.equals(v2);
        case GE: {
            Number n1 = asNumber(v1);
            Number n2 = asNumber(v2);
            if (n1 instanceof Double || n2 instanceof Double) {
                return asDouble(v1) >= asDouble(v2);
            }
            if (n1 instanceof Long || n2 instanceof Long) {
                return asLong(v1) >= asLong(v2);
            }
            if (n1 instanceof Integer || n2 instanceof Integer) {
                return asInt(v1) >= asInt(v2);
            }
            throw new IllegalStateException("unsupported numbers " + n1.getClass() + " and " + n2.getClass());
        }
        case GT: {
            Number n1 = asNumber(v1);
            Number n2 = asNumber(v2);
            if (n1 instanceof Double || n2 instanceof Double) {
                return asDouble(v1) > asDouble(v2);
            }
            if (n1 instanceof Long || n2 instanceof Long) {
                return asLong(v1) > asLong(v2);
            }
            if (n1 instanceof Integer || n2 instanceof Integer) {
                return asInt(v1) > asInt(v2);
            }
            throw new IllegalStateException("unsupported numbers " + n1.getClass() + " and " + n2.getClass());
        }
        case LE: {
            Number n1 = asNumber(v1);
            Number n2 = asNumber(v2);
            if (n1 instanceof Double || n2 instanceof Double) {
                return asDouble(v1) <= asDouble(v2);
            }
            if (n1 instanceof Long || n2 instanceof Long) {
                return asLong(v1) <= asLong(v2);
            }
            if (n1 instanceof Integer || n2 instanceof Integer) {
                return asInt(v1) <= asInt(v2);
            }
            throw new IllegalStateException("unsupported numbers " + n1.getClass() + " and " + n2.getClass());
        }
        case LT: {
            Number n1 = asNumber(v1);
            Number n2 = asNumber(v2);
            if (n1 instanceof Double || n2 instanceof Double) {
                return asDouble(v1) < asDouble(v2);
            }
            if (n1 instanceof Long || n2 instanceof Long) {
                return asLong(v1) < asLong(v2);
            }
            if (n1 instanceof Integer || n2 instanceof Integer) {
                return asInt(v1) < asInt(v2);
            }
            throw new IllegalStateException("unsupported numbers " + n1.getClass() + " and " + n2.getClass());
        }
        default:
            throw new IllegalStateException("unsupported op " + op);
        }
    }

    @Override
    public String toString() {
        return left.toString() + ' ' + op.sign + ' ' + right.toString();
    }
}
