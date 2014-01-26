package com.fasterxml.jackson.jsonpath;

import java.text.ParseException;
import java.util.List;

import com.fasterxml.jackson.jsonpath.internal.JsonPathExpression;

public abstract class JsonPathFunctionParser {

    public abstract String getName();

    public abstract JsonPathFunction parse(int position, List<JsonPathExpression> arguments) throws ParseException;

    protected void checkNumberOfArg(int position, List<JsonPathExpression> arguments, int n) throws ParseException {
        if (arguments.size() != n) {
            throw new ParseException(getName() + " is expecting exactly " + n + " argument" + (n > 1 ? "s" : "")
                    + " but got " + arguments.size(), position);
        }
    }

}
