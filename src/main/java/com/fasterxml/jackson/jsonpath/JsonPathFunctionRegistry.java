package com.fasterxml.jackson.jsonpath;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.jsonpath.internal.func.LenJPF;
import com.fasterxml.jackson.jsonpath.internal.func.PosJPF;
import com.fasterxml.jackson.jsonpath.internal.func.TypeOfJPF;

public class JsonPathFunctionRegistry {

    public static final JsonPathFunctionRegistry DEFAULT = new JsonPathFunctionRegistry();
    static {
        DEFAULT.register(TypeOfJPF.instance);
        DEFAULT.register(PosJPF.instance);
        DEFAULT.register(LenJPF.instance);
    }

    private final Map<String, JsonPathFunction> functions = new HashMap<String, JsonPathFunction>();

    public void register(JsonPathFunction function) {
        functions.put(function.getName(), function);
    }

    public Map<String, JsonPathFunction> getFunctions() {
        return functions;
    }
}
