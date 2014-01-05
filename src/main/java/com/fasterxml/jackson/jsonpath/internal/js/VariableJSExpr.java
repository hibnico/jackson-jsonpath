package com.fasterxml.jackson.jsonpath.internal.js;

public class VariableJSExpr extends JSExpr {

    private String id;

    public VariableJSExpr(String id) {
        this.id = id;
    }
}
