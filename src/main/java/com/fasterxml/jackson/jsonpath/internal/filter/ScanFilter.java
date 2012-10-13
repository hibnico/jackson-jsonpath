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
package com.fasterxml.jackson.jsonpath.internal.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class ScanFilter extends PathTokenFilter {

    public ScanFilter(String condition) {
        super(condition);
    }

    @Override
    public JsonNode filter(JsonNode node) {
        ArrayNode result = JsonNodeFactory.instance.arrayNode();
        scan(node, result);
        return result;
    }

    @Override
    public boolean isArrayFilter() {
        return true;
    }

    @Override
    public JsonNode getRef(JsonNode node) {
        throw new UnsupportedOperationException();
    }

    private void scan(JsonNode node, ArrayNode result) {
        if (node.isObject()) {
            result.add(node);
            for (JsonNode value : node) {
                if (value.isContainerNode()) {
                    scan(value, result);
                }
            }
        } else if (node.isArray()) {
            for (JsonNode value : node) {
                if (value.isContainerNode()) {
                    scan(value, result);
                }
            }
        }
    }
}
