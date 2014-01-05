package com.fasterxml.jackson.jsonpath.internal.js;

public class CompareJSExpr extends JSExpr {

    enum CompareOp {
        EQ, NE, LT, GT, LE, GE
    }

    private JSExpr left;

    private JSExpr right;

    private CompareOp op;

    public CompareJSExpr(CompareOp op, JSExpr left, JSExpr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }
}
