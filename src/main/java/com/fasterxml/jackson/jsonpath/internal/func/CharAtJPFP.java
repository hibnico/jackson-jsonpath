package com.fasterxml.jackson.jsonpath.internal.func;

import java.text.ParseException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.jsonpath.JsonPathFunction;
import com.fasterxml.jackson.jsonpath.JsonPathFunctionParser;
import com.fasterxml.jackson.jsonpath.JsonPathRuntimeException;
import com.fasterxml.jackson.jsonpath.internal.JsonPathContext;
import com.fasterxml.jackson.jsonpath.internal.JsonPathExpression;
import com.fasterxml.jackson.jsonpath.internal.TypeMismatchException;

public class CharAtJPFP extends JsonPathFunctionParser {

    public static final CharAtJPFP instance = new CharAtJPFP();

    @Override
    public String getName() {
        return "charAt";
    }

    @Override
    public JsonPathFunction parse(final int position, List<JsonPathExpression> arguments) throws ParseException {
        checkNumberOfArg(position, arguments, 2);
        return new JsonPathFunction() {
            @Override
            protected Object callAsObject(JsonPathContext context, List<JsonNode> args) {
                JsonNode text = args.get(0);
                if (!text.isTextual()) {
                    throw new TypeMismatchException(position, JsonNodeType.STRING, text, "the first argument of ",
                            getName());
                }
                JsonNode index = args.get(1);
                if (!index.isNumber()) {
                    throw new TypeMismatchException(position, JsonNodeType.NUMBER, index, "the second argument of ",
                            getName());
                }
                String t = text.asText();
                int n = index.asInt();
                if (n >= t.length()) {
                    throw new JsonPathRuntimeException("index out of bound " + n + " > " + (t.length() - 1), position);
                }
                if (n < -text.size()) {
                    throw new JsonPathRuntimeException("index out of bound " + n + " < " + (-t.length()), position);
                }
                if (n < 0) {
                    return t.charAt(t.length() + n);
                }
                return t.charAt(n);
            }
        };
    }
}
