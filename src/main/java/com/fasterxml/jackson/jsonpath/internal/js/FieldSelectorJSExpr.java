package com.fasterxml.jackson.jsonpath.internal.js;

public class FieldSelectorJSExpr extends JSExpr {

    private JSExpr object;

    private String field;

    public FieldSelectorJSExpr(JSExpr object, String field) {
        this.object = object;
        this.field = field;
    }
}
