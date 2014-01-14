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

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonNodeUtil;
import com.fasterxml.jackson.jsonpath.JsonPathMultiValue;

class JsonWildcardSelectorEvaluator extends JsonPathEvaluator {

    @Override
    public JsonPathMultiValue eval(JsonNode node) {
        JsonPathMultiValue ret = new JsonPathMultiValue();
        if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                ret.add(JsonNodeUtil.objectNode(i, node.get(i)));
            }
        } else if (node.isObject()) {
            for (Iterator<String> it = node.fieldNames(); it.hasNext();) {
                String field = it.next();
                ret.add(JsonNodeUtil.objectNode(field, node.get(field)));
            }
        } else {
            throw new IllegalStateException("Wildcard cannot apply on: " + node);
        }
        return ret;
    }

}
