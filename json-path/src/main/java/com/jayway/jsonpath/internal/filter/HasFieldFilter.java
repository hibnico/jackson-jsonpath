/*
 * Copyright 2011 the original author or authors.
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
package com.jayway.jsonpath.internal.filter;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;

/**
 * @author Kalle Stenflo
 */
public class HasFieldFilter extends PathTokenFilter {

    public HasFieldFilter(String condition) {
        super(condition);
    }

    @Override
    public JsonNode filter(JsonNode node) {

        // [?(@.isbn)]
        ArrayNode result = JsonNodeFactory.instance.arrayNode();

        String trimmedCondition = condition;

        if (condition.contains("['")) {
            trimmedCondition = trimmedCondition.replace("['", ".");
            trimmedCondition = trimmedCondition.replace("']", "");
        }

        trimmedCondition = trim(trimmedCondition, 5, 2);

        for (JsonNode item : node) {
            if (item.isObject()) {
                if (item.has(trimmedCondition)) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    @Override
    public JsonNode getRef(JsonNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isArrayFilter() {
        return true;
    }
}
