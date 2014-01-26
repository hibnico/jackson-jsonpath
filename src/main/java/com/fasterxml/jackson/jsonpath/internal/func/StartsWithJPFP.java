package com.fasterxml.jackson.jsonpath.internal.func;

import java.text.ParseException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.jsonpath.JsonPathFunction;
import com.fasterxml.jackson.jsonpath.JsonPathFunctionParser;
import com.fasterxml.jackson.jsonpath.internal.JsonPathContext;
import com.fasterxml.jackson.jsonpath.internal.JsonPathExpression;
import com.fasterxml.jackson.jsonpath.internal.TypeMismatchException;

public class StartsWithJPFP extends JsonPathFunctionParser {

    public static final StartsWithJPFP instance = new StartsWithJPFP();

    @Override
    public String getName() {
        return "startsWith";
    }

    @Override
    public JsonPathFunction parse(final int position, List<JsonPathExpression> arguments) throws ParseException {
        checkNumberOfArg(position, arguments, 2);
        return new JsonPathFunction() {
            @Override
            protected Object callAsObject(JsonPathContext context, List<JsonNode> args) {
                if (!args.get(0).isTextual()) {
                    throw new TypeMismatchException(position, JsonNodeType.STRING, args.get(0),
                            "the first argument of ", getName());
                }
                String text = args.get(0).asText();

                if (!args.get(1).isTextual()) {
                    throw new TypeMismatchException(position, JsonNodeType.STRING, args.get(1),
                            "the second argument of ", getName());
                }
                String startText = args.get(1).asText();

                return text.startsWith(startText);
            }
        };
    }
}
