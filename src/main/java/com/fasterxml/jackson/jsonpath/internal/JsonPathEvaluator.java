package com.fasterxml.jackson.jsonpath.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPathValue;

public abstract class JsonPathEvaluator {

    public abstract JsonPathValue eval(JsonNode node);

    public boolean isMulti() {
        return false;
    }

}
