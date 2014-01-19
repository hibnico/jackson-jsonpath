package com.fasterxml.jackson.jsonpath.internal;

import com.fasterxml.jackson.jsonpath.JsonPathRuntimeException;

public class NullJsonException extends JsonPathRuntimeException {

    NullJsonException(int position) {
        super("null value", position);
    }

}
