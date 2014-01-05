package com.fasterxml.jackson.jsonpath.internal;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonIndexArrayEvaluator extends JsonArrayEvaluator {

    private int n;

    public JsonIndexArrayEvaluator(int n) {
        this.n = n;
    }

    @Override
    public int getIndex(JsonNode node) {
        return n;
    }
}
