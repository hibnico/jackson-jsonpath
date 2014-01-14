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

public class ShiftJSExpr extends JSExpr {

    enum ShiftOp {
        LEFT("<<"), RIGHT(">>"), LOGICAL_RIGHT(">>>");

        private String sign;

        private ShiftOp(String sign) {
            this.sign = sign;
        }
    }

    private JSExpr left;

    private JSExpr right;

    private ShiftOp op;

    public ShiftJSExpr(ShiftOp op, JSExpr left, JSExpr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public Object eval(JsonNode node) {
        Number l = asNumber(left.eval(node));
        Number r = asNumber(right.eval(node));
        switch (op) {
        case LEFT:
            if (l instanceof Long) {
                if (r instanceof Long) {
                    return ((Long) l) << ((Long) r);
                } else if (r instanceof Integer) {
                    return ((Long) l) << ((Integer) r);
                } else {
                    throw new IllegalStateException("invalid right number " + r.getClass());
                }
            } else if (l instanceof Integer) {
                if (r instanceof Long) {
                    return ((Integer) l) << ((Long) r);
                } else if (r instanceof Integer) {
                    return ((Integer) l) << ((Integer) r);
                } else {
                    throw new IllegalStateException("invalid right number " + r.getClass());
                }
            } else {
                throw new IllegalStateException("invalid left number " + l.getClass());
            }
        case RIGHT:
            if (l instanceof Long) {
                if (r instanceof Long) {
                    return ((Long) l) >> ((Long) r);
                } else if (r instanceof Integer) {
                    return ((Long) l) >> ((Integer) r);
                } else {
                    throw new IllegalStateException("invalid right number " + r.getClass());
                }
            } else if (l instanceof Integer) {
                if (r instanceof Long) {
                    return ((Integer) l) >> ((Long) r);
                } else if (r instanceof Integer) {
                    return ((Integer) l) >> ((Integer) r);
                } else {
                    throw new IllegalStateException("invalid right number " + r.getClass());
                }
            } else {
                throw new IllegalStateException("invalid left number " + l.getClass());
            }
        case LOGICAL_RIGHT:
            if (l instanceof Long) {
                if (r instanceof Long) {
                    return ((Long) l) >>> ((Long) r);
                } else if (r instanceof Integer) {
                    return ((Long) l) >>> ((Integer) r);
                } else {
                    throw new IllegalStateException("invalid right number " + r.getClass());
                }
            } else if (l instanceof Integer) {
                if (r instanceof Long) {
                    return ((Integer) l) >>> ((Long) r);
                } else if (r instanceof Integer) {
                    return ((Integer) l) >>> ((Integer) r);
                } else {
                    throw new IllegalStateException("invalid right number " + r.getClass());
                }
            } else {
                throw new IllegalStateException("invalid left number " + l.getClass());
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
