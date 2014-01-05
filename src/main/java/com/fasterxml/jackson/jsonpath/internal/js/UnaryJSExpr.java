package com.fasterxml.jackson.jsonpath.internal.js;

import com.fasterxml.jackson.databind.JsonNode;

public class UnaryJSExpr extends JSExpr {

    enum UnaryOp {
        NOT, PLUS, MINUS, NOT_BITWISE
    }

    private JSExpr expr;

    private UnaryOp op;

    public UnaryJSExpr(UnaryOp op, JSExpr expr) {
        this.op = op;
        this.expr = expr;
    }

    @Override
    public Object eval(JsonNode node)  {
        boolean v = expr.evalBoolean(node);
        return !v;
    }

}
