package com.fasterxml.jackson.jsonpath.internal.js;

public class ArithmeticJSExpr extends JSExpr {

    enum ArithmeticOp {
        PLUS, MINUS, MULT, DIV, MODULO
    }

    private JSExpr left;

    private JSExpr right;

    private ArithmeticOp op;

    public ArithmeticJSExpr(ArithmeticOp op, JSExpr left, JSExpr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }
}
