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

public class UnaryJSExpr extends JSExpr {

    enum UnaryOp {
        NOT("!"), PLUS("+"), MINUS("-"), NOT_BITWISE("~");

        private String sign;

        private UnaryOp(String sign) {
            this.sign = sign;
        }
    }

    private JSExpr expr;

    private UnaryOp op;

    public UnaryJSExpr(UnaryOp op, JSExpr expr) {
        this.op = op;
        this.expr = expr;
    }

    @Override
    public Object eval(JsonNode node) {
        Object v = expr.eval(node);
        switch (op) {
        case NOT:
            return !asBoolean(v);
        case MINUS: {
            Number n = asNumber(v);
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
            return asNumber(v);
        case NOT_BITWISE: {
            Number n = asNumber(v);
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
        return op.sign + expr.toString();
    }
}
