package com.fasterxml.jackson.jsonpath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.internal.JsonPathEvaluator;

public abstract class JsonPathValue {

    public abstract JsonPathValue apply(JsonPathEvaluator evaluator);

    public abstract void addTo(JsonPathMultiValue value);

    public abstract JsonNode toNode();
}
