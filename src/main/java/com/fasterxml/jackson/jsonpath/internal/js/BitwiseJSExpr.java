package com.fasterxml.jackson.jsonpath.internal.js;

public class BitwiseJSExpr extends JSExpr {

    enum BitwiseOp {
        OR, AND, XOR
    }

    private JSExpr left;

    private JSExpr right;

    private BitwiseOp op;

    public BitwiseJSExpr(BitwiseOp op, JSExpr left, JSExpr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }
}
