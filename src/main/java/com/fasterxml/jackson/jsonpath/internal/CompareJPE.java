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

import java.text.ParseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPathValue;

class CompareJPE extends JsonPathExpression {

    enum CompareOp {
        EQ("=="), NE("!="), LT("<"), GT(">"), LE("<="), GE(">=");

        private String sign;

        private CompareOp(String sign) {
            this.sign = sign;
        }
    }

    private CompareOp op;

    private JsonPathExpression left;

    private JsonPathExpression right;

    CompareJPE(int position, CompareOp op, JsonPathExpression left, JsonPathExpression right) throws ParseException {
        super(position);
        this.op = op;
        this.left = left;
        this.right = right;

        // check vector compliance
        switch (op) {
        case EQ:
        case NE:
            // OK
            break;
        case GE:
        case GT:
        case LE:
        case LT:
            if (left.isVector() && right.isVector()) {
                throw new ParseException("left and right cannot be both vectors on comparaison operator " + op.sign,
                        position);
            }
            break;
        default:
            throw new IllegalStateException("unsupported op " + op);
        }
    }

    @Override
    boolean isVector() {
        switch (op) {
        case EQ:
        case NE:
            return left.isVector() || right.isVector();
        case GE:
        case GT:
        case LE:
        case LT:
            return left.isVector() && !right.isVector() || !left.isVector() && right.isVector();
        default:
            throw new IllegalStateException("unsupported op " + op);
        }
    }

    @Override
    public JsonPathValue eval(JsonPathContext context) {
        return evalAsDotProduct(context, left, right);
    }

    @Override
    Object computeObject(JsonPathContext context, JsonNode[] childValues) {
        switch (op) {
        case EQ:
            return childValues[0].equals(childValues[1]);
        case NE:
            return !childValues[0].equals(childValues[1]);
        case GE: {
            Number n1 = asNumber(childValues[0], "compare op '", op.sign, "'");
            Number n2 = asNumber(childValues[1], "compare op '", op.sign, "'");
            if (n1 instanceof Double || n2 instanceof Double) {
                return n1.doubleValue() >= n2.doubleValue();
            }
            if (n1 instanceof Long || n2 instanceof Long) {
                return n1.longValue() >= n2.longValue();
            }
            if (n1 instanceof Integer || n2 instanceof Integer) {
                return n1.intValue() >= n2.intValue();
            }
            throw new IllegalStateException("unsupported number type " + n1.getClass() + " and/or " + n2.getClass());
        }
        case GT: {
            Number n1 = asNumber(childValues[0], "compare op '", op.sign, "'");
            Number n2 = asNumber(childValues[1], "compare op '", op.sign, "'");
            if (n1 instanceof Double || n2 instanceof Double) {
                return n1.doubleValue() > n2.doubleValue();
            }
            if (n1 instanceof Long || n2 instanceof Long) {
                return n1.longValue() > n2.longValue();
            }
            if (n1 instanceof Integer || n2 instanceof Integer) {
                return n1.intValue() > n2.intValue();
            }
            throw new IllegalStateException("unsupported number type " + n1.getClass() + " and/or " + n2.getClass());
        }
        case LE: {
            Number n1 = asNumber(childValues[0], "compare op '", op.sign, "'");
            Number n2 = asNumber(childValues[1], "compare op '", op.sign, "'");
            if (n1 instanceof Double || n2 instanceof Double) {
                return n1.doubleValue() <= n2.doubleValue();
            }
            if (n1 instanceof Long || n2 instanceof Long) {
                return n1.longValue() <= n2.longValue();
            }
            if (n1 instanceof Integer || n2 instanceof Integer) {
                return n1.intValue() <= n2.intValue();
            }
            throw new IllegalStateException("unsupported number type " + n1.getClass() + " and/or " + n2.getClass());
        }
        case LT: {
            Number n1 = asNumber(childValues[0], "compare op '", op.sign, "'");
            Number n2 = asNumber(childValues[1], "compare op '", op.sign, "'");
            if (n1 instanceof Double || n2 instanceof Double) {
                return n1.intValue() < n2.intValue();
            }
            if (n1 instanceof Long || n2 instanceof Long) {
                return n1.longValue() < n2.longValue();
            }
            if (n1 instanceof Integer || n2 instanceof Integer) {
                return n1.intValue() < n2.intValue();
            }
            throw new IllegalStateException("unsupported number type " + n1.getClass() + " and/or " + n2.getClass());
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
