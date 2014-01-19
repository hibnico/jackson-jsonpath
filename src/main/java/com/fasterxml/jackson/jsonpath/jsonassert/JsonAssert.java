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
package com.fasterxml.jackson.jsonpath.jsonassert;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.hamcrest.Matcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jsonpath.JsonPathValue;
import com.fasterxml.jackson.jsonpath.jsonassert.impl.AsNodeMatcher;
import com.fasterxml.jackson.jsonpath.jsonassert.impl.AsObjectMatcher;
import com.fasterxml.jackson.jsonpath.jsonassert.impl.IsMultiValueMatcher;
import com.fasterxml.jackson.jsonpath.jsonassert.impl.IsNoValueMatcher;
import com.fasterxml.jackson.jsonpath.jsonassert.impl.IsSingleValueMatcher;
import com.fasterxml.jackson.jsonpath.jsonassert.impl.JsonAsserterImpl;
import com.fasterxml.jackson.jsonpath.jsonassert.impl.matcher.CollectionMatcher;
import com.fasterxml.jackson.jsonpath.jsonassert.impl.matcher.IsCollectionWithSize;
import com.fasterxml.jackson.jsonpath.jsonassert.impl.matcher.IsEmptyCollection;
import com.fasterxml.jackson.jsonpath.jsonassert.impl.matcher.IsMapContainingKey;
import com.fasterxml.jackson.jsonpath.jsonassert.impl.matcher.IsMapContainingValue;

public class JsonAssert {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonAsserter withResource(String jsonResource) throws JsonProcessingException, IOException {
        return with(JsonAssert.class.getClassLoader().getResource(jsonResource));
    }

    public static JsonAsserter with(String json) throws JsonProcessingException, IOException {
        return with(mapper.readTree(json));
    }

    public static JsonAsserter with(Reader reader) throws JsonProcessingException, IOException {
        return with(mapper.readTree(reader));
    }

    public static JsonAsserter with(InputStream is) throws JsonProcessingException, IOException {
        return with(mapper.readTree(is));
    }

    public static JsonAsserter with(URL url) throws JsonProcessingException, IOException {
        return with(mapper.readTree(url));
    }

    public static JsonAsserter with(JsonNode node) throws IOException {
        return new JsonAsserterImpl(node);
    }

    // Matchers

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static CollectionMatcher<?> collectionWithSize(Matcher<? super Integer> sizeMatcher) {
        return new IsCollectionWithSize(sizeMatcher);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Matcher<Map<String, ?>> mapContainingKey(Matcher<String> keyMatcher) {
        return new IsMapContainingKey(keyMatcher);
    }

    public static <V> Matcher<? super Map<?, V>> mapContainingValue(Matcher<? super V> valueMatcher) {
        return new IsMapContainingValue<V>(valueMatcher);
    }

    public static Matcher<Collection<Object>> emptyCollection() {
        return new IsEmptyCollection<Object>();
    }

    public static Matcher<JsonPathValue> isMutliValue() {
        return new IsMultiValueMatcher();
    }

    public static Matcher<JsonPathValue> isSingleValue() {
        return new IsSingleValueMatcher();
    }

    public static Matcher<JsonPathValue> isNoValue() {
        return new IsNoValueMatcher();
    }

    public static Matcher<JsonPathValue> asNode(Matcher<JsonNode> nodeMatcher) {
        return new AsNodeMatcher(nodeMatcher);
    }

    public static Matcher<JsonPathValue> asObject(Matcher<?> matcher) {
        return new AsObjectMatcher(matcher);
    }

}
