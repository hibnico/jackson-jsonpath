package com.fasterxml.jackson.jsonpath.internal.func;

import java.text.ParseException;
import java.util.List;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.jsonpath.JsonPathFunction;
import com.fasterxml.jackson.jsonpath.JsonPathFunctionParser;
import com.fasterxml.jackson.jsonpath.internal.JsonPathContext;
import com.fasterxml.jackson.jsonpath.internal.JsonPathExpression;
import com.fasterxml.jackson.jsonpath.internal.LiteralJPE;
import com.fasterxml.jackson.jsonpath.internal.TypeMismatchException;

public class RegexpMatchJPFP extends JsonPathFunctionParser {

    public static final RegexpMatchJPFP instance = new RegexpMatchJPFP();

    @Override
    public String getName() {
        return "regexpMatch";
    }

    @Override
    public JsonPathFunction parse(final int position, List<JsonPathExpression> arguments) throws ParseException {
        checkNumberOfArg(position, arguments, 2);
        final Pattern pattern;
        if (arguments.get(0) instanceof LiteralJPE) {
            // try to compile the regexp pattern earlier
            JsonNode literal = ((LiteralJPE) arguments.get(0)).getLiteral();
            if (!literal.isTextual()) {
                throw new ParseException("Expecting a string as first argument", position);
            }
            pattern = Pattern.compile(literal.asText());
        } else {
            pattern = null;
        }
        return new JsonPathFunction() {
            @Override
            protected Object callAsObject(JsonPathContext context, List<JsonNode> args) {
                if (args.get(1).isMissingNode()) {
                    return args.get(1);
                }

                if (!args.get(1).isTextual()) {
                    throw new TypeMismatchException(position, JsonNodeType.STRING, args.get(1),
                            "the second argument of ", getName());
                }

                Pattern p = pattern;
                if (p == null) {
                    if (!args.get(0).isTextual()) {
                        throw new TypeMismatchException(position, JsonNodeType.STRING, args.get(0),
                                "the first argument of ", getName());
                    }
                    p = Pattern.compile(args.get(0).asText());
                }

                String text = args.get(1).asText();

                return p.matcher(text).matches();
            }
        };
    }
}
