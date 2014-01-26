package com.fasterxml.jackson.jsonpath.internal.func;

import java.text.ParseException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPathFunction;
import com.fasterxml.jackson.jsonpath.JsonPathFunctionParser;
import com.fasterxml.jackson.jsonpath.internal.JsonPathContext;
import com.fasterxml.jackson.jsonpath.internal.JsonPathExpression;

public class PosJPFP extends JsonPathFunctionParser {

    public static final PosJPFP instance = new PosJPFP();

    @Override
    public String getName() {
        return "pos";
    }

    @Override
    public JsonPathFunction parse(int position, List<JsonPathExpression> arguments) throws ParseException {
        checkNumberOfArg(position, arguments, 0);
        return new JsonPathFunction() {
            @Override
            protected Object callAsObject(JsonPathContext context, List<JsonNode> args) {
                return context.getPos();
            }
        };
    }

}
