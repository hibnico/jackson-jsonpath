package com.fasterxml.jackson.jsonpath.internal.js;

import com.fasterxml.jackson.databind.JsonNode;

public class NotJSExpr extends JSExpr {

    private JSExpr expr;

    public NotJSExpr(JSExpr expr) {
        this.expr = expr;
    }

    @Override
    public Object eval(JsonNode node)  {
        boolean v = expr.evalBoolean(node);
        return !v;
    }

}
