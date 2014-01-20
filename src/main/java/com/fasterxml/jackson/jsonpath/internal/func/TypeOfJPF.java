package com.fasterxml.jackson.jsonpath.internal.func;

import java.text.ParseException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPathFunction;
import com.fasterxml.jackson.jsonpath.internal.JsonPathContext;
import com.fasterxml.jackson.jsonpath.internal.JsonPathExpression;

public class TypeOfJPF extends JsonPathFunction {

    public static final TypeOfJPF instance = new TypeOfJPF();

    @Override
    public String getName() {
        return "typeof";
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
        return args.get(0).getNodeType().toString().toLowerCase();
    }
}
