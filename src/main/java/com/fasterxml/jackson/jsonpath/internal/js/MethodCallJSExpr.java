package com.fasterxml.jackson.jsonpath.internal.js;

import java.util.List;

public class MethodCallJSExpr extends JSExpr {

    private JSExpr object;

    private String function;

    private List<JSExpr> arguments;

    public MethodCallJSExpr(JSExpr object, String function, List<JSExpr> arguments) {
        this.object = object;
        this.function = function;
        this.arguments = arguments;
    }
}
