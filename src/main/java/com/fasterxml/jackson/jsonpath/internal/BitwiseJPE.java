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
package com.fasterxml.jackson.jsonpath.internal;

import com.fasterxml.jackson.databind.JsonNode;

class BitwiseJPE extends JsonPathExpression {

    enum BitwiseOp {
        OR("|"), AND("&"), XOR("^");

        private String sign;

        private BitwiseOp(String sign) {
            this.sign = sign;
        }
    }

    private BitwiseOp op;

    BitwiseJPE(int position, BitwiseOp op, JsonPathExpression left, JsonPathExpression right) {
        super(position, left, right);
        this.op = op;
    }

    @Override
    Object computeObject(JsonPathContext context, JsonNode[] childValues) {
        Number n1 = asNumber(childValues[0], "bitwise op '", op.sign, "'");
        Number n2 = asNumber(childValues[1], "bitwise op '", op.sign, "'");
        switch (op) {
        case AND:
            if (n1 instanceof Long || n2 instanceof Long) {
                return n1.longValue() & n2.longValue();
            }
            if (n1 instanceof Integer || n2 instanceof Integer) {
                return n1.intValue() & n2.intValue();
            }
            throw new UnsupportedTypeException(position, op.sign, n1, n2);
        case OR:
            if (n1 instanceof Long || n2 instanceof Long) {
                return n1.longValue() | n2.longValue();
            }
            if (n1 instanceof Integer || n2 instanceof Integer) {
                return n1.intValue() | n2.intValue();
            }
            throw new UnsupportedTypeException(position, op.sign, n1, n2);
        case XOR:
            if (n1 instanceof Long || n2 instanceof Long) {
                return n1.longValue() ^ n2.longValue();
            }
            if (n1 instanceof Integer || n2 instanceof Integer) {
                return n1.intValue() ^ n2.intValue();
            }
            throw new UnsupportedTypeException(position, op.sign, n1, n2);
        default:
            throw new IllegalStateException("unsupported op: " + op.sign);
        }
    }

    @Override
    public String toString() {
        return children[0].toString() + ' ' + op.sign + ' ' + children[1].toString();
    }
}
