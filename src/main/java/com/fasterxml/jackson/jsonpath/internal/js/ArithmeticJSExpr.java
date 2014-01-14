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

public class ArithmeticJSExpr extends JSExpr {

    enum ArithmeticOp {
        PLUS("+"), MINUS("-"), MULT("*"), DIV("/"), MODULO("%");

        private String sign;

        private ArithmeticOp(String sign) {
            this.sign = sign;
        }

    }

    private JSExpr left;

    private JSExpr right;

    private ArithmeticOp op;

    public ArithmeticJSExpr(ArithmeticOp op, JSExpr left, JSExpr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public Object eval(JsonNode node) {
        Object v1 = left.eval(node);
        Object v2 = right.eval(node);

        Number n1 = asLenientNumber(v1);
        Number n2 = asLenientNumber(v2);

        switch (op) {
        case PLUS:
            String s1 = asLenientString(v1);
            String s2 = asLenientString(v2);
            if (s1 != null || s2 != null) {
                return asString(v1) + asString(v2);
            }
            if (n1 instanceof Double || n2 instanceof Double) {
                return asDouble(v1) + asDouble(v2);
            }
            if (n1 instanceof Long || n2 instanceof Long) {
                return asLong(v1) + asLong(v2);
            }
            if (n1 instanceof Integer || n1 instanceof Integer) {
                return asInt(v1) + asInt(v2);
            }
            throw new IllegalStateException("not plussable " + v1 + " and " + v2);
        case MINUS:
            if (n1 instanceof Double || n2 instanceof Double) {
                return asDouble(v1) - asDouble(v2);
            }
            if (n1 instanceof Long || n2 instanceof Long) {
                return asLong(v1) - asLong(v2);
            }
            if (n1 instanceof Integer || n1 instanceof Integer) {
                return asInt(v1) - asInt(v2);
            }
            throw new IllegalStateException("not minussable " + v1 + " and " + v2);
        case DIV:
            if (n1 instanceof Double || n2 instanceof Double) {
                return asDouble(v1) / asDouble(v2);
            }
            if (n1 instanceof Long || n2 instanceof Long) {
                return asLong(v1) / asLong(v2);
            }
            if (n1 instanceof Integer || n1 instanceof Integer) {
                return asInt(v1) / asInt(v2);
            }
            throw new IllegalStateException("not divisable " + v1 + " and " + v2);
        case MULT:
            if (n1 instanceof Double || n2 instanceof Double) {
                return asDouble(v1) * asDouble(v2);
            }
            if (n1 instanceof Long || n2 instanceof Long) {
                return asLong(v1) * asLong(v2);
            }
            if (n1 instanceof Integer || n1 instanceof Integer) {
                return asInt(v1) * asInt(v2);
            }
            throw new IllegalStateException("not multipliable " + v1 + " and " + v2);
        case MODULO:
            if (n1 instanceof Double || n2 instanceof Double) {
                return asDouble(v1) % asDouble(v2);
            }
            if (n1 instanceof Long || n2 instanceof Long) {
                return asLong(v1) % asLong(v2);
            }
            if (n1 instanceof Integer || n1 instanceof Integer) {
                return asInt(v1) % asInt(v2);
            }
            throw new IllegalStateException("not modulable " + v1 + " and " + v2);
        default:
            throw new IllegalStateException("illegal operator " + op);
        }
    }

    @Override
    public String toString() {
        return left.toString() + ' ' + op.sign + ' ' + right.toString();
    };
}
