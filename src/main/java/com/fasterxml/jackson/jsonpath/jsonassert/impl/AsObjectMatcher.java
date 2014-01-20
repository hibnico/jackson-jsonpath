/*
 * Copyright 2014 the original author or authors.
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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jsonpath.JsonPathNoValue;
import com.fasterxml.jackson.jsonpath.JsonPathValue;

public class AsObjectMatcher extends BaseMatcher<JsonPathValue> {

    private Matcher<?> matcher;

    private static final ObjectMapper mapper = new ObjectMapper();

    public AsObjectMatcher(Matcher<?> matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean matches(Object item) {
        if (!(item instanceof JsonPathValue)) {
            return false;
        }
        if (item instanceof JsonPathNoValue) {
            return false;
        }
        JsonNode node = ((JsonPathValue) item).asNode();
        Object obj;
        try {
            obj = mapper.treeToValue(node, Object.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return matcher.matches(obj);
    }

    @Override
    public void describeTo(Description description) {
        description.appendDescriptionOf(matcher);
    }

}
