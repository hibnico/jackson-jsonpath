package com.fasterxml.jackson.jsonpath.internal.func;

import java.text.ParseException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPathFunction;
import com.fasterxml.jackson.jsonpath.internal.JsonPathContext;
import com.fasterxml.jackson.jsonpath.internal.JsonPathExpression;

public class PosJPF extends JsonPathFunction {

    public static final PosJPF instance = new PosJPF();

    @Override
    public String getName() {
        return "pos";
    }

    @Override
    public void check(int position, List<JsonPathExpression> arguments) throws ParseException {
        checkArgNumber(position, arguments, 0);
    }

    @Override
    public boolean isVector(List<JsonPathExpression> arguments) {
        return false;
    }

    @Override
    protected Object callAsObject(JsonPathContext context, List<JsonNode> args) {
        return context.getPos();
    }
}
