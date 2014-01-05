package com.fasterxml.jackson.jsonpath.internal.js;

public class ArraySelectorJSExpr extends JSExpr {

    private JSExpr array;

    private JSExpr index;

    public ArraySelectorJSExpr(JSExpr array, JSExpr index) {
        this.array = array;
        this.index = index;
    }

}
