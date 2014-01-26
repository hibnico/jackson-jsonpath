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

public class SubstringJPFP extends JsonPathFunctionParser {

    public static final SubstringJPFP instance = new SubstringJPFP();

    @Override
    public String getName() {
        return "substring";
    }

    @Override
    public JsonPathFunction parse(final int position, List<JsonPathExpression> arguments) throws ParseException {
        if (arguments.size() < 2 || arguments.size() > 3) {
            throw new ParseException(getName() + " is expecting 2 or 3 arguments but got " + arguments.size(), position);
        }
        return new JsonPathFunction() {
            @Override
            protected Object callAsObject(JsonPathContext context, List<JsonNode> args) {
                if (!args.get(0).isTextual()) {
                    throw new TypeMismatchException(position, JsonNodeType.STRING, args.get(0),
                            "the first argument of ", getName());
                }
                String text = args.get(0).asText();

                if (!args.get(1).isNumber()) {
                    throw new TypeMismatchException(position, JsonNodeType.NUMBER, args.get(1),
                            "the second argument of ", getName());
                }
                int startIndex = args.get(1).asInt();

                Integer endIndex = null;
                if (args.size() > 2) {
                    if (!args.get(2).isNumber()) {
                        throw new TypeMismatchException(position, JsonNodeType.NUMBER, args.get(2),
                                "the third argument of ", getName());
                    }
                    endIndex = args.get(2).asInt();
                }

                if (endIndex == null) {
                    return text.substring(startIndex);
                }
                return text.substring(startIndex, endIndex);
            }
        };
    }
}
