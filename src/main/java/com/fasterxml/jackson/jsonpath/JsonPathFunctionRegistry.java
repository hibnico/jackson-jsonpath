package com.fasterxml.jackson.jsonpath;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.jsonpath.internal.func.CharAtJPFP;
import com.fasterxml.jackson.jsonpath.internal.func.EndsWithJPFP;
import com.fasterxml.jackson.jsonpath.internal.func.LenJPFP;
import com.fasterxml.jackson.jsonpath.internal.func.PosJPFP;
import com.fasterxml.jackson.jsonpath.internal.func.RegexpMatchJPFP;
import com.fasterxml.jackson.jsonpath.internal.func.StartsWithJPFP;
import com.fasterxml.jackson.jsonpath.internal.func.SubstringJPFP;
import com.fasterxml.jackson.jsonpath.internal.func.TypeOfJPFP;

public class JsonPathFunctionRegistry {

    public static final JsonPathFunctionRegistry DEFAULT = new JsonPathFunctionRegistry() {
        {
            registerDefaultFunctions();
        }
    };

    private final Map<String, JsonPathFunctionParser> functions = new HashMap<String, JsonPathFunctionParser>();

    public void registerDefaultFunctions() {
        register(TypeOfJPFP.instance);
        register(PosJPFP.instance);
        register(LenJPFP.instance);
        register(CharAtJPFP.instance);
        register(SubstringJPFP.instance);
        register(StartsWithJPFP.instance);
        register(EndsWithJPFP.instance);
        register(RegexpMatchJPFP.instance);
    }

    public void register(JsonPathFunctionParser function) {
        functions.put(function.getName(), function);
    }

    public Map<String, JsonPathFunctionParser> getFunctions() {
        return functions;
    }
}
