package com.fasterxml.jackson.jsonpath.internal.js;

public class ShiftJSExpr extends JSExpr {

    enum ShiftOp {
        LEFT, RIGHT, LOGICAL_RIGHT
    }

    private JSExpr left;

    private JSExpr right;

    private ShiftOp op;

    public ShiftJSExpr(ShiftOp op, JSExpr left, JSExpr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }
}
