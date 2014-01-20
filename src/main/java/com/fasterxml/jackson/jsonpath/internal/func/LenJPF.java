package com.fasterxml.jackson.jsonpath.internal.func;

import java.text.ParseException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPathFunction;
import com.fasterxml.jackson.jsonpath.internal.JsonPathContext;
import com.fasterxml.jackson.jsonpath.internal.JsonPathExpression;

public class LenJPF extends JsonPathFunction {

    public static final LenJPF instance = new LenJPF();

    @Override
    public String getName() {
        return "len";
    }

    @Override
    public void check(int position, List<JsonPathExpression> arguments) throws ParseException {
        checkArgNumber(position, arguments, 1);
    }

    @Override
    public boolean isVector(List<JsonPathExpression> arguments) {
        return false;
    }

    @Override
    protected Object callAsObject(JsonPathContext context, List<JsonNode> args) {
        JsonNode node = args.get(0);
        if (node.isTextual()) {
            return node.asText().length();
        }
        return node.size();
    }
}
