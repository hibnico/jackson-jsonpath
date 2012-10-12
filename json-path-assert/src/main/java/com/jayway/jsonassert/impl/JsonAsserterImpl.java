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
package com.jayway.jsonassert.impl;

import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.codehaus.jackson.JsonNode;
import org.hamcrest.Matcher;

import com.jayway.jsonassert.JsonAsserter;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonNodeUtil;
import com.jayway.jsonpath.JsonPath;

public class JsonAsserterImpl implements JsonAsserter {

    private final JsonNode jsonObject;

    /**
     * Instantiates a new JSONAsserter
     * 
     * @param jsonObject the object to make asserts on
     */
    public JsonAsserterImpl(JsonNode jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public <T> JsonAsserter assertThat(String path, Matcher<T> matcher) {
        JsonNode node = JsonPath.read(jsonObject, path);
        Object obj = JsonNodeUtil.asJava(node);
        if (!matcher.matches(obj)) {
            throw new AssertionError(String.format("JSON doesn't match.\nExpected:\n%s\nActual:\n%s", matcher.toString(), obj));
        }
        return this;
    }

    @Override
    public <T> JsonAsserter assertEquals(String path, T expected) {
        return assertThat(path, equalTo(expected));
    }

    @Override
    public JsonAsserter assertNotDefined(String path) {
        try {
            JsonPath.read(jsonObject, path);
            throw new AssertionError(format("Document contains the path <%s> but was expected not to.", path));
        } catch (InvalidPathException e) {
        }
        return this;
    }

    @Override
    public JsonAsserter assertNull(String path) {
        return assertThat(path, nullValue());
    }

    @Override
    public <T> JsonAsserter assertNotNull(String path) {
        return assertThat(path, notNullValue());
    }

    @Override
    public JsonAsserter and() {
        return this;
    }

}
