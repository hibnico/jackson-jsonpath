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
package com.fasterxml.jackson.jsonpath;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jsonpath.internal.JsonPathEvaluator;

public class JsonPath {

    private ObjectMapper jsonMapper = new ObjectMapper();

    private List<JsonPathEvaluator> evaluators;

    JsonPath(List<JsonPathEvaluator> evaluators) {
        this.evaluators = evaluators;
    }

    public JsonPathValue eval(JsonNode node) {
        if (node == null || node.isNull()) {
            return JsonPathNoValue.INSTANCE;
        }

        JsonPathValue value = new JsonPathSingleValue(node);
        for (JsonPathEvaluator evaluator : evaluators) {
            value = value.apply(evaluator);
        }

        return value;
    }

    public JsonPathValue eval(String json) throws JsonProcessingException, IOException {
        return eval(jsonMapper.readTree(json));
    }

    public JsonPathValue eval(URL jsonURL) throws IOException {
        return eval(jsonMapper.readTree(jsonURL));
    }

    public JsonPathValue eval(File jsonFile) throws IOException {
        if (!jsonFile.exists()) {
            throw new IllegalArgumentException("json file does not exist");
        }
        return eval(jsonMapper.readTree(jsonFile));
    }

    public JsonPathValue eval(InputStream jsonInputStream) throws IOException {
        if (jsonInputStream == null) {
            throw new IllegalArgumentException("json input stream can not be null");
        }
        return eval(jsonMapper.readTree(jsonInputStream));
    }

    public static JsonPath compile(String path) throws ParseException {
        return JsonPathCompiler.DEFAULT.compile(path);
    }

    public static JsonPathValue eval(JsonNode node, String path) throws ParseException {
        return compile(path).eval(node);
    }

    public static JsonPathValue eval(String json, String path) throws JsonProcessingException, IOException,
            ParseException {
        return compile(path).eval(json);
    }

    public static JsonPathValue eval(URL jsonURL, String path) throws IOException, ParseException {
        return compile(path).eval(jsonURL);
    }

    public static JsonPathValue eval(File jsonFile, String path) throws IOException, ParseException {
        return compile(path).eval(jsonFile);
    }

    public static JsonPathValue eval(InputStream jsonInputStream, String path) throws IOException, ParseException {
        return compile(path).eval(jsonInputStream);
    }
}
