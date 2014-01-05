package com.fasterxml.jackson.jsonpath;

import java.text.ParseException;
import java.util.List;

import com.fasterxml.jackson.jsonpath.internal.JsonPathEvaluator;
import com.fasterxml.jackson.jsonpath.internal.JsonPathParser;
import com.fasterxml.jackson.jsonpath.internal.js.JavascriptCompiler;
import com.fasterxml.jackson.jsonpath.internal.js.JavascriptNopCompiler;

public class JsonPathCompiler {

    static final JsonPathCompiler DEFAULT = new JsonPathCompiler(new JavascriptNopCompiler());

    private JavascriptCompiler jsCompiler;

    public JsonPathCompiler(JavascriptCompiler jsCompiler) {
        this.jsCompiler = jsCompiler;
    }

    public JsonPath compile(String path) throws ParseException {
        JsonPathParser parser = new JsonPathParser(jsCompiler, path);
        parser.parse();
        List<JsonPathEvaluator> evaluators = parser.getEvaluators();
        return new JsonPath(evaluators);
    }

}
