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

    public BitwiseJPE(BitwiseOp op, JsonPathExpression left, JsonPathExpression right) {
        super(left, right);
        this.op = op;
    }

    @Override
    Object computeObject(JsonPathContext context, JsonNode[] childValues) {
        Number v1 = asNumber(childValues[0]);
        Number v2 = asNumber(childValues[1]);
        switch (op) {
        case AND:
            if (v1 instanceof Long || v2 instanceof Long) {
                return v1.longValue() & v2.longValue();
            }
            if (v1 instanceof Integer || v2 instanceof Integer) {
                return v1.intValue() & v2.intValue();
            }
            throw new IllegalStateException("unsupported types " + v1.getClass() + " and " + v2.getClass());
        case OR:
            if (v1 instanceof Long || v2 instanceof Long) {
                return v1.longValue() | v2.longValue();
            }
            if (v1 instanceof Integer || v2 instanceof Integer) {
                return v1.intValue() | v2.intValue();
            }
            throw new IllegalStateException("unsupported types " + v1.getClass() + " and " + v2.getClass());
        case XOR:
            if (v1 instanceof Long || v2 instanceof Long) {
                return v1.longValue() ^ v2.longValue();
            }
            if (v1 instanceof Integer || v2 instanceof Integer) {
                return v1.intValue() ^ v2.intValue();
            }
            throw new IllegalStateException("unsupported types " + v1.getClass() + " and " + v2.getClass());
        default:
            throw new IllegalStateException("unsupported op");
        }
    }

    @Override
    public String toString() {
        return children[0].toString() + ' ' + op.sign + ' ' + children[1].toString();
    }
}
