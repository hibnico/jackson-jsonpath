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
import com.fasterxml.jackson.jsonpath.jsonassert.impl.JsonAsserterImpl;
import com.fasterxml.jackson.jsonpath.jsonassert.impl.matcher.CollectionMatcher;
import com.fasterxml.jackson.jsonpath.jsonassert.impl.matcher.IsCollectionWithSize;
import com.fasterxml.jackson.jsonpath.jsonassert.impl.matcher.IsEmptyCollection;
import com.fasterxml.jackson.jsonpath.jsonassert.impl.matcher.IsMapContainingKey;
import com.fasterxml.jackson.jsonpath.jsonassert.impl.matcher.IsMapContainingValue;

public class JsonAssert {

    /**
     * Creates a JSONAsserter
     * 
     * @param jsonResource the resource in the classpath to read json from
     * @return a JSON asserter initialized with the provided document
     * @throws IOException
     * @throws JsonProcessingException
     */
    public static JsonAsserter withResource(String jsonResource) throws JsonProcessingException, IOException {
        return withResource(jsonResource, new ObjectMapper());
    }

    /**
     * Creates a JSONAsserter
     * 
     * @param jsonResource the resource in the classpath to read json from
     * @param mapper the Jackson ObjectMapper to use to read and to map to Java object
     * @return a JSON asserter initialized with the provided document
     * @throws IOException
     * @throws JsonProcessingException
     */
    public static JsonAsserter withResource(String jsonResource, ObjectMapper mapper) throws JsonProcessingException, IOException {
        return withResource(jsonResource, JsonAssert.class, new ObjectMapper());
    }

    /**
     * Creates a JSONAsserter
     * 
     * @param jsonResource the resource in the classpath to read json from
     * @param cl the class to load the resource from
     * @param mapper the Jackson ObjectMapper to use to read and to map to Java object
     * @return a JSON asserter initialized with the provided document
     * @throws IOException
     * @throws JsonProcessingException
     */
    public static JsonAsserter withResource(String jsonResource, Class< ? > cl, ObjectMapper mapper) throws JsonProcessingException, IOException {
        return withResource(jsonResource, JsonAssert.class.getClassLoader(), new ObjectMapper());
    }

    /**
     * Creates a JSONAsserter
     * 
     * @param jsonResource the resource in the classpath to read json from
     * @param cl the class loader to use to load the resource
     * @param mapper the Jackson ObjectMapper to use to read and to map to Java object
     * @return a JSON asserter initialized with the provided document
     * @throws IOException
     * @throws JsonProcessingException
     */
    public static JsonAsserter withResource(String jsonResource, ClassLoader cl, ObjectMapper mapper) throws JsonProcessingException, IOException {
        return new JsonAsserterImpl(mapper.readTree(cl.getResource(jsonResource)), mapper);
    }

    /**
     * Creates a JSONAsserter
     * 
     * @param json the JSON document to create a JSONAsserter for
     * @return a JSON asserter initialized with the provided document
     * @throws IOException
     * @throws JsonProcessingException
     */
    public static JsonAsserter with(String json) throws JsonProcessingException, IOException {
        return with(json, new ObjectMapper());
    }

    /**
     * Creates a JSONAsserter
     * 
     * @param json the JSON document to create a JSONAsserter for
     * @param mapper the Jackson ObjectMapper to use to read and to map to Java object
     * @return a JSON asserter initialized with the provided document
     * @throws IOException
     * @throws JsonProcessingException
     */
    public static JsonAsserter with(String json, ObjectMapper mapper) throws JsonProcessingException, IOException {
        return new JsonAsserterImpl(mapper.readTree(json), mapper);
    }

    /**
     * Creates a JSONAsserter
     * 
     * @param reader the reader of the json document
     * @return a JSON asserter initialized with the provided document
     * @throws IOException
     * @throws JsonProcessingException
     */
    public static JsonAsserter with(Reader reader) throws JsonProcessingException, IOException {
        return with(reader, new ObjectMapper());
    }

    /**
     * Creates a JSONAsserter
     * 
     * @param reader the reader of the json document
     * @param mapper the Jackson ObjectMapper to use to read and to map to Java object
     * @return a JSON asserter initialized with the provided document
     * @throws IOException
     * @throws JsonProcessingException
     */
    public static JsonAsserter with(Reader reader, ObjectMapper mapper) throws JsonProcessingException, IOException {
        return new JsonAsserterImpl(mapper.readTree(reader), mapper);
    }

    /**
     * Creates a JSONAsserter
     * 
     * @param is the input stream
     * @return a JSON asserter initialized with the provided document
     * @throws IOException
     * @throws JsonProcessingException
     */
    public static JsonAsserter with(InputStream is) throws JsonProcessingException, IOException {
        return with(is, new ObjectMapper());
    }

    /**
     * Creates a JSONAsserter
     * 
     * @param is the input stream
     * @param mapper the Jackson ObjectMapper to use to read and to map to Java object
     * @return a JSON asserter initialized with the provided document
     * @throws IOException
     * @throws JsonProcessingException
     */
    public static JsonAsserter with(InputStream is, ObjectMapper mapper) throws JsonProcessingException, IOException {
        return new JsonAsserterImpl(mapper.readTree(is), mapper);
    }

    /**
     * Creates a JSONAsserter
     * 
     * @param is url of the json file to read
     * @return a JSON asserter initialized with the provided document
     * @throws IOException
     * @throws JsonProcessingException
     */
    public static JsonAsserter with(URL url) throws JsonProcessingException, IOException {
        return with(url, new ObjectMapper());
    }

    /**
     * Creates a JSONAsserter
     * 
     * @param is url of the json file to read
     * @param mapper the Jackson ObjectMapper to use to read and to map to Java object
     * @return a JSON asserter initialized with the provided document
     * @throws IOException
     * @throws JsonProcessingException
     */
    public static JsonAsserter with(URL url, ObjectMapper mapper) throws JsonProcessingException, IOException {
        return new JsonAsserterImpl(mapper.readTree(url), mapper);
    }

    /**
     * Creates a JSONAsserter
     * 
     * @param is url of the json file to read
     * @return a JSON asserter initialized with the provided document
     * @throws IOException
     * @throws JsonProcessingException
     */
    public static JsonAsserter with(JsonNode node) throws IOException {
        return with(node, new ObjectMapper());
    }

    /**
     * Creates a JSONAsserter
     * 
     * @param is url of the json file to read
     * @param mapper the Jackson ObjectMapper to use to read and to map to Java object
     * @return a JSON asserter initialized with the provided document
     * @throws IOException
     * @throws JsonProcessingException
     */
    public static JsonAsserter with(JsonNode node, ObjectMapper mapper) throws IOException {
        return new JsonAsserterImpl(node, mapper);
    }

    // Matchers

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static CollectionMatcher< ? > collectionWithSize(Matcher< ? super Integer> sizeMatcher) {
        return new IsCollectionWithSize(sizeMatcher);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Matcher<Map<String, ? >> mapContainingKey(Matcher<String> keyMatcher) {
        return new IsMapContainingKey(keyMatcher);
    }

    public static <V> Matcher< ? super Map< ? , V>> mapContainingValue(Matcher< ? super V> valueMatcher) {
        return new IsMapContainingValue<V>(valueMatcher);
    }

    public static Matcher<Collection<Object>> emptyCollection() {
        return new IsEmptyCollection<Object>();
    }

}
