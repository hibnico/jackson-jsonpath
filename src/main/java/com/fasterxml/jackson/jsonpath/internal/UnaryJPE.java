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

class UnaryJPE extends JsonPathExpression {

    enum UnaryOp {
        NOT("!"), PLUS("+"), MINUS("-"), NOT_BITWISE("~");

        private String sign;

        private UnaryOp(String sign) {
            this.sign = sign;
        }
    }

    private UnaryOp op;

    public UnaryJPE(UnaryOp op, JsonPathExpression expr) {
        super(expr);
        this.op = op;
    }

    @Override
    Object computeObject(JsonPathContext context, JsonNode[] childValues) {
        switch (op) {
        case NOT:
            return !asBoolean(childValues[0]);
        case MINUS: {
            Number n = asNumber(childValues[0]);
            if (n instanceof Double) {
                return -((Double) n);
            }
            if (n instanceof Long) {
                return -((Long) n);
            }
            if (n instanceof Integer) {
                return -((Integer) n);
            }
            throw new IllegalStateException("unsupported number " + n.getClass());
        }
        case PLUS:
            return asNumber(childValues[0]);
        case NOT_BITWISE: {
            Number n = asNumber(childValues[0]);
            if (n instanceof Long) {
                return ~((Long) n);
            }
            if (n instanceof Integer) {
                return ~((Integer) n);
            }
            throw new IllegalStateException("unsupported number " + n.getClass());
        }
        default:
            throw new IllegalStateException("unsupported op " + op);
        }
    }

    @Override
    public String toString() {
        return op.sign + children[0].toString();
    }
}
