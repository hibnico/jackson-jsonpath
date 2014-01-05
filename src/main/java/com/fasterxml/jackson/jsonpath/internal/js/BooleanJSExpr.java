package com.fasterxml.jackson.jsonpath.internal.js;

public class BooleanJSExpr extends JSExpr {

    enum BooleanOp {
        OR, AND, XOR
    }

    private JSExpr left;

    private JSExpr right;

    private BooleanOp op;

    public BooleanJSExpr(BooleanOp op, JSExpr left, JSExpr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }
}
