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

class ArithmeticJPE extends JsonPathExpression {

    enum ArithmeticOp {
        PLUS("+"), MINUS("-"), MULT("*"), DIV("/"), MODULO("%");

        private String sign;

        private ArithmeticOp(String sign) {
            this.sign = sign;
        }

    }

    private ArithmeticOp op;

    private JsonPathExpression left;
    
    private JsonPathExpression right;

    ArithmeticJPE(int position, ArithmeticOp op, JsonPathExpression left, JsonPathExpression right) {
        super(position, isVectorFromDotProduct(left, right));
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public JsonPathValue eval(JsonPathContext context) {
        return evalAsDotProduct(context, left, right);
    }

    @Override
    public Object computeObject(JsonPathContext context, JsonNode[] childValues) {
        switch (op) {
        case PLUS: {
            String s1 = asLenientString(childValues[0]);
            String s2 = asLenientString(childValues[1]);
            if (s1 != null || s2 != null) {
                return s1 + s2;
            }
            Number n1 = asNumber(childValues[0], "arithmetic op '", op.sign, "'");
            Number n2 = asNumber(childValues[1], "arithmetic op '", op.sign, "'");
            if (n1 instanceof Double || n2 instanceof Double) {
                return n1.doubleValue() + n2.doubleValue();
            }
            if (n1 instanceof Long || n2 instanceof Long) {
                return n1.longValue() + n2.longValue();
            }
            if (n1 instanceof Integer || n2 instanceof Integer) {
                return n1.intValue() + n2.intValue();
            }
            throw new IllegalStateException("unsupported number type " + n1.getClass() + " and/or " + n2.getClass());
        }
        case MINUS: {
            Number n1 = asNumber(childValues[0], "arithmetic op '", op.sign, "'");
            Number n2 = asNumber(childValues[1], "arithmetic op '", op.sign, "'");
            if (n1 instanceof Double || n2 instanceof Double) {
                return n1.doubleValue() - n2.doubleValue();
            }
            if (n1 instanceof Long || n2 instanceof Long) {
                return n1.longValue() - n2.longValue();
            }
            if (n1 instanceof Integer || n2 instanceof Integer) {
                return n1.intValue() - n2.intValue();
            }
            throw new IllegalStateException("unsupported number type " + n1.getClass() + " and/or " + n2.getClass());
        }
        case DIV: {
            Number n1 = asNumber(childValues[0], "arithmetic op '", op.sign, "'");
            Number n2 = asNumber(childValues[1], "arithmetic op '", op.sign, "'");
            if (n1 instanceof Double || n2 instanceof Double) {
                return n1.doubleValue() / n2.doubleValue();
            }
            if (n1 instanceof Long || n2 instanceof Long) {
                return n1.longValue() / n2.longValue();
            }
            if (n1 instanceof Integer || n2 instanceof Integer) {
                return n1.intValue() / n2.intValue();
            }
            throw new IllegalStateException("unsupported number type " + n1.getClass() + " and/or " + n2.getClass());
        }
        case MULT: {
            Number n1 = asNumber(childValues[0], "arithmetic op '", op.sign, "'");
            Number n2 = asNumber(childValues[1], "arithmetic op '", op.sign, "'");

            if (n1 instanceof Double || n2 instanceof Double) {
                return n1.doubleValue() * n2.doubleValue();
            }
            if (n1 instanceof Long || n2 instanceof Long) {
                return n1.longValue() * n2.longValue();
            }
            if (n1 instanceof Integer || n2 instanceof Integer) {
                return n1.intValue() * n2.intValue();
            }
            throw new IllegalStateException("unsupported number type " + n1.getClass() + " and/or " + n2.getClass());
        }
        case MODULO: {
            Number n1 = asNumber(childValues[0], "arithmetic op '", op.sign, "'");
            Number n2 = asNumber(childValues[1], "arithmetic op '", op.sign, "'");

            if (n1 instanceof Double || n2 instanceof Double) {
                return n1.doubleValue() % n2.doubleValue();
            }
            if (n1 instanceof Long || n2 instanceof Long) {
                return n1.longValue() % n2.longValue();
            }
            if (n1 instanceof Integer || n2 instanceof Integer) {
                return n1.intValue() % n2.intValue();
            }
            throw new IllegalStateException("unsupported number type " + n1.getClass() + " and/or " + n2.getClass());
        }
        default:
            throw new IllegalStateException("illegal operator " + op.sign);
        }
    }

    @Override
    public String toString() {
        return left.toString() + ' ' + op.sign + ' ' + right.toString();
    }

}
