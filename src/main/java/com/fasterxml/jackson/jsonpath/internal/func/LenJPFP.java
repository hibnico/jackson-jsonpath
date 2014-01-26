package com.fasterxml.jackson.jsonpath.internal.func;

import java.text.ParseException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPathFunction;
import com.fasterxml.jackson.jsonpath.JsonPathFunctionParser;
import com.fasterxml.jackson.jsonpath.internal.JsonPathContext;
import com.fasterxml.jackson.jsonpath.internal.JsonPathExpression;

public class LenJPFP extends JsonPathFunctionParser {

    public static final LenJPFP instance = new LenJPFP();

    @Override
    public String getName() {
        return "len";
    }

    @Override
    public JsonPathFunction parse(int position, List<JsonPathExpression> arguments) throws ParseException {
        checkNumberOfArg(position, arguments, 1);
        return new JsonPathFunction() {
            @Override
            protected Object callAsObject(JsonPathContext context, List<JsonNode> args) {
                JsonNode node = args.get(0);
                if (node.isTextual()) {
                    return node.asText().length();
                }
                return node.size();
            }
        };
    }

}
