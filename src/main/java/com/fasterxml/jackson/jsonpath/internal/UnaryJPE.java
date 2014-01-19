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
import com.fasterxml.jackson.jsonpath.JsonPathValue;

class UnaryJPE extends JsonPathExpression {

    enum UnaryOp {
        NOT("!"), PLUS("+"), MINUS("-"), NOT_BITWISE("~");

        private String sign;

        private UnaryOp(String sign) {
            this.sign = sign;
        }
    }

    private UnaryOp op;

    private JsonPathExpression expr;

    UnaryJPE(int position, UnaryOp op, JsonPathExpression expr) {
        super(position, expr.isVector());
        this.op = op;
        this.expr = expr;
    }

    @Override
    public JsonPathValue eval(JsonPathContext context) {
        return evalAsDotProduct(context, expr);
    }

    @Override
    Object computeObject(JsonPathContext context, JsonNode[] childValues) {
        switch (op) {
        case NOT:
            return !asBoolean(childValues[0], "unary op '", op.sign, "'");
        case MINUS: {
            Number n = asNumber(childValues[0], "unary op '", op.sign, "'");
            if (n instanceof Double) {
                return -((Double) n);
            }
            if (n instanceof Long) {
                return -((Long) n);
            }
            if (n instanceof Integer) {
                return -((Integer) n);
            }
            throw new UnsupportedTypeException(position, op.sign, n);
        }
        case PLUS:
            return asNumber(childValues[0], "unary op '", op.sign, "'");
        case NOT_BITWISE: {
            Number n = asNumber(childValues[0], "unary op '", op.sign, "'");
            if (n instanceof Long) {
                return ~((Long) n);
            }
            if (n instanceof Integer) {
                return ~((Integer) n);
            }
            throw new UnsupportedTypeException(position, op.sign, n);
        }
        default:
            throw new IllegalStateException("unsupported op " + op);
        }
    }

    @Override
    public String toString() {
        return op.sign + expr.toString();
    }
}
