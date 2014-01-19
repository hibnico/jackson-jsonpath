/*
 * Copyright 2012-2014 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fasterxml.jackson.jsonpath.jsonassert.impl;

import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.text.ParseException;

import org.hamcrest.Matcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPath;
import com.fasterxml.jackson.jsonpath.JsonPathRuntimeException;
import com.fasterxml.jackson.jsonpath.JsonPathValue;
import com.fasterxml.jackson.jsonpath.jsonassert.JsonAsserter;

public class JsonAsserterImpl implements JsonAsserter {

    private final JsonNode jsonObject;

    public JsonAsserterImpl(JsonNode jsonObject) {
        this.jsonObject = jsonObject;
    }

    public <T> JsonAsserter assertThat(String path, Matcher<T> matcher) {
        JsonPathValue value;
        try {
            value = JsonPath.eval(jsonObject, path);
        } catch (ParseException e) {
            AssertionError error = new AssertionError(String.format("Invalid json path: " + e.getMessage()
                    + ". Error at:\n" + path + "\n%" + e.getErrorOffset() + "s^", ""));
            error.initCause(e);
            throw error;
        } catch (JsonPathRuntimeException e) {
            AssertionError error = new AssertionError(String.format("Runtime error: " + e.getMessage()
                    + ". Error at:\n" + path + "\n%" + e.getPosition() + "s^", ""));
            error.initCause(e);
            throw error;
        }
        if (!matcher.matches(value)) {
            throw new AssertionError(String.format("JSON doesn't match.\nExpected:\n%s\nActual:\n%s",
                    matcher.toString(), value));
        }
        return this;
    }

    public <T> JsonAsserter assertEquals(String path, T expected) {
        return assertThat(path, equalTo(expected));
    }

    public JsonAsserter assertNotDefined(String path) {
        try {
            JsonNode res = JsonPath.eval(jsonObject, path).toNode();
            if (!res.isNull()) {
                throw new AssertionError(format("Document contains the path <%s> but was expected not to.", path));
            }
        } catch (ParseException e) {
            AssertionError error = new AssertionError(String.format("Invalid json path: " + e.getMessage()
                    + ". Error at:\n" + path + "\n%" + e.getErrorOffset() + "s^", ""));
            error.initCause(e);
            throw error;
        } catch (JsonPathRuntimeException e) {
            AssertionError error = new AssertionError(String.format("Runtime error: " + e.getMessage()
                    + ". Error at:\n" + path + "\n%" + e.getPosition() + "s^", ""));
            error.initCause(e);
            throw error;
        }
        return this;
    }

    public JsonAsserter assertNull(String path) {
        return assertThat(path, nullValue());
    }

    public <T> JsonAsserter assertNotNull(String path) {
        return assertThat(path, notNullValue());
    }

    public JsonAsserter and() {
        return this;
    }

}
