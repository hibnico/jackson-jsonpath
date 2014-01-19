package com.fasterxml.jackson.jsonpath;

public class JsonPathRuntimeException extends RuntimeException {

    private int position;

    public JsonPathRuntimeException(String message, int position) {
        super(message);
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
