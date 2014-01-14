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

public class BitwiseJSExpr extends JSExpr {

    enum BitwiseOp {
        OR("|"), AND("&"), XOR("^");

        private String sign;

        private BitwiseOp(String sign) {
            this.sign = sign;
        }
    }

    private JSExpr left;

    private JSExpr right;

    private BitwiseOp op;

    public BitwiseJSExpr(BitwiseOp op, JSExpr left, JSExpr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public Object eval(JsonNode node) {
        Object v1 = asNumber(left.eval(node));
        Object v2 = asNumber(right.eval(node));
        switch (op) {
        case AND:
            if (v1 instanceof Long || v2 instanceof Long) {
                return asLong(v1) & asLong(v2);
            }
            if (v1 instanceof Integer || v2 instanceof Integer) {
                return asInt(v1) & asInt(v2);
            }
            throw new IllegalStateException("unsupported types " + v1.getClass() + " and " + v2.getClass());
        case OR:
            if (v1 instanceof Long || v2 instanceof Long) {
                return asLong(v1) | asLong(v2);
            }
            if (v1 instanceof Integer || v2 instanceof Integer) {
                return asInt(v1) | asInt(v2);
            }
            throw new IllegalStateException("unsupported types " + v1.getClass() + " and " + v2.getClass());
        case XOR:
            if (v1 instanceof Long || v2 instanceof Long) {
                return asLong(v1) ^ asLong(v2);
            }
            if (v1 instanceof Integer || v2 instanceof Integer) {
                return asInt(v1) ^ asInt(v2);
            }
            throw new IllegalStateException("unsupported types " + v1.getClass() + " and " + v2.getClass());
        default:
            throw new IllegalStateException("unsupported op");
        }
    }

    @Override
    public String toString() {
        return left.toString() + ' ' + op.sign + ' ' + right.toString();
    }
}
