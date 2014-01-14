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

public class BooleanJSExpr extends JSExpr {

    enum BooleanOp {
        OR("||"), AND("&&");

        private String sign;

        private BooleanOp(String sign) {
            this.sign = sign;
        }
    }

    private JSExpr left;

    private JSExpr right;

    private BooleanOp op;

    public BooleanJSExpr(BooleanOp op, JSExpr left, JSExpr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public Object eval(JsonNode node) {
        boolean b1 = asBoolean(left.eval(node));
        boolean b2 = asBoolean(right.eval(node));
        switch (op) {
        case AND:
            return b1 && b2;
        case OR:
            return b1 || b2;
        default:
            throw new IllegalStateException("unsupported op " + op);
        }
    }

    @Override
    public String toString() {
        return left.toString() + ' ' + op.sign + ' ' + right.toString();
    }
}
