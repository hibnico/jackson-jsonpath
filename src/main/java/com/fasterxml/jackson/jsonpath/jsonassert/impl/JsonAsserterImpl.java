/*
 * Copyright 2012 the original author or authors.
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

import org.hamcrest.Matcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jsonpath.InvalidPathException;
import com.fasterxml.jackson.jsonpath.JsonPath;
import com.fasterxml.jackson.jsonpath.jsonassert.JsonAsserter;

public class JsonAsserterImpl implements JsonAsserter {

    private final JsonNode jsonObject;

    private final ObjectMapper mapper;

    /**
     * Instantiates a new JSONAsserter
     * 
     * @param jsonObject the object to make asserts on
     */
    public JsonAsserterImpl(JsonNode jsonObject, ObjectMapper mapper) {
        this.jsonObject = jsonObject;
        this.mapper = mapper;
    }

    public <T> JsonAsserter assertThat(String path, Matcher<T> matcher) {
        JsonNode node = JsonPath.read(jsonObject, path);
        Object obj;
        try {
            obj = mapper.treeToValue(node, Object.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (!matcher.matches(obj)) {
            throw new AssertionError(String.format("JSON doesn't match.\nExpected:\n%s\nActual:\n%s", matcher.toString(), obj));
        }
        return this;
    }

    public <T> JsonAsserter assertEquals(String path, T expected) {
        return assertThat(path, equalTo(expected));
    }

    public JsonAsserter assertNotDefined(String path) {
        try {
            JsonPath.read(jsonObject, path);
            throw new AssertionError(format("Document contains the path <%s> but was expected not to.", path));
        } catch (InvalidPathException e) {
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
