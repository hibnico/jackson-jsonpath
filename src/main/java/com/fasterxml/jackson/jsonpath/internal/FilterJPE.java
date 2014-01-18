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

class FilterJPE extends JsonPathExpression {

    private JsonPathExpression filter;

    public FilterJPE(JsonPathExpression object, JsonPathExpression filter) {
        super(object);
        this.filter = filter;
    }

    @Override
    JsonNode computeNode(JsonPathContext context, JsonNode[] childValues) {
        for (JsonNode subNode : childValues[0]) {
            boolean select = filter.evalAsBoolean(new JsonPathContext(context, subNode));
            if (select) {
                return subNode;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return children[0].toString() + "[?(" + filter.toString() + ")]";
    }
}
