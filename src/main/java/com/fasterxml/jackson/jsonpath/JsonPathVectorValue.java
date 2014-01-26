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
package com.fasterxml.jackson.jsonpath;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class JsonPathVectorValue extends JsonPathValue {

    public static final JsonPathVectorValue EMPTY = new JsonPathVectorValue();

    private ArrayNode nodes = JsonNodeFactory.instance.arrayNode();

    private List<String> names = new ArrayList<String>();

    @Override
    public void addTo(JsonPathVectorValue ret) {
        int i = 0;
        for (JsonNode node : nodes) {
            ret.add(node, names.get(i++));
        }
    }

    public void add(JsonNode node, String name) {
        if (!node.isMissingNode()) {
            nodes.add(node);
            names.add(name);
        }
    }

    @Override
    public ArrayNode asNode() {
        return nodes;
    }

    @Override
    public Iterable<JsonNode> getNodes() {
        return nodes;
    }

    public String getName(int i) {
        return names.get(i);
    }

}
