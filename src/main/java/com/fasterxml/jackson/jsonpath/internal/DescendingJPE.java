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
package com.fasterxml.jackson.jsonpath.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPathMultiValue;
import com.fasterxml.jackson.jsonpath.JsonPathValue;

class DescendingJPE extends JsonPathExpression {

    public DescendingJPE(JsonPathExpression object) {
        super(object);
    }

    @Override
    JsonPathValue compute(JsonPathContext context, JsonNode[] childValues) {
        JsonPathMultiValue ret = new JsonPathMultiValue();
        descend(childValues[0], ret);
        return ret;
    }

    private void descend(JsonNode node, JsonPathMultiValue result) {
        if (node.isObject()) {
            result.add(node);
            for (JsonNode value : node) {
                if (value.isContainerNode()) {
                    descend(value, result);
                }
            }
        } else if (node.isArray()) {
            for (JsonNode value : node) {
                if (value.isContainerNode()) {
                    descend(value, result);
                }
            }
        }
    }
}
