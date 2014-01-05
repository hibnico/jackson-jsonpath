package com.fasterxml.jackson.jsonpath.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.internal.js.JavascriptExpression;

public class JsonScriptArrayEvaluator extends JsonArrayEvaluator {

    private JavascriptExpression expression;

    public JsonScriptArrayEvaluator(JavascriptExpression expression) {
        this.expression = expression;
    }

    @Override
    public int getIndex(JsonNode node) {
        return expression.evalInt(node);
    }

}
