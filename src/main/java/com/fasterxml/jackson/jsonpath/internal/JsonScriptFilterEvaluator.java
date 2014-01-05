package com.fasterxml.jackson.jsonpath.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPathNoValue;
import com.fasterxml.jackson.jsonpath.JsonPathSingleValue;
import com.fasterxml.jackson.jsonpath.JsonPathValue;
import com.fasterxml.jackson.jsonpath.internal.js.JavascriptExpression;

public class JsonScriptFilterEvaluator extends JsonPathEvaluator {

    private JavascriptExpression expression;

    public JsonScriptFilterEvaluator(JavascriptExpression expression) {
        this.expression = expression;
    }

    @Override
    public JsonPathValue eval(JsonNode node) {
        boolean select = expression.evalBoolean(node);
        if (select) {
            return new JsonPathSingleValue(node);
        }
        return JsonPathNoValue.INSTANCE;
    }

}
