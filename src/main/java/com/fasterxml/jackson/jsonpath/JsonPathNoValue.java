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

import java.util.Collections;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;

public class JsonPathNoValue extends JsonPathValue {

    public static final JsonPathNoValue INSTANCE = new JsonPathNoValue();

    @Override
    public void addTo(JsonPathVectorValue ret) {
        // nothing to do
    }

    @Override
    public JsonNode asNode() {
        return MissingNode.getInstance();
    }

    @Override
    public Iterable<JsonNode> getNodes() {
        return Collections.emptySet();
    }
}
