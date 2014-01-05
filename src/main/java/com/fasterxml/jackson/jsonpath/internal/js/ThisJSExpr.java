package com.fasterxml.jackson.jsonpath.internal.js;

import com.fasterxml.jackson.databind.JsonNode;

public class ThisJSExpr extends JSExpr {

    @Override
    public Object eval(JsonNode node) {
        return node;
    }
}
