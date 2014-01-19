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

import static com.fasterxml.jackson.jsonpath.jsonassert.JsonAssert.asObject;
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

    @Override
    public <T> JsonAsserter assertThat(String path, Matcher<T> matcher) {
        JsonPathValue value;
        try {
            value = JsonPath.eval(jsonObject, path);
        } catch (ParseException e) {
            AssertionError error = new AssertionError(String.format(
                    "Invalid json path: %s. Error at:\n%s\n%" + e.getErrorOffset() + "s^", e.getMessage(), path, ""));
            error.initCause(e);
            throw error;
        } catch (JsonPathRuntimeException e) {
            AssertionError error = new AssertionError(String.format(
                    "Runtime error: %s. Error at:\n%s\n%" + e.getPosition() + "s^", e.getMessage(), path, ""));
            error.initCause(e);
            throw error;
        }
        if (!matcher.matches(value)) {
            throw new AssertionError(String.format("JSON doesn't match.\nExpected:\n%s\nActual:\n%s",
                    matcher.toString(), value));
        }
        return this;
    }

    @Override
    public <T> JsonAsserter assertEquals(String path, T expected) {
        return assertThat(path, asObject(equalTo(expected)));
    }

    @Override
    public JsonAsserter assertNull(String path) {
        return assertThat(path, asObject(nullValue()));
    }

    @Override
    public <T> JsonAsserter assertNotNull(String path) {
        return assertThat(path, asObject(notNullValue()));
    }

    @Override
    public JsonAsserter and() {
        return this;
    }

}
