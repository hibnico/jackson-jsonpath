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

import java.util.LinkedList;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.InvalidPathException;

/**
 * @author Kalle Stenflo
 */
public class FieldFilter extends PathTokenFilter {

    public FieldFilter(String condition) {
        super(condition);
    }

    @Override
    public JsonNode filter(JsonNode node, LinkedList<Filter> filters, boolean inArrayContext) {
        if (node.isArray()) {
            if (!inArrayContext) {
                return null;
            } else {
                ArrayNode result = JsonNodeFactory.instance.arrayNode();
                for (JsonNode current : node) {
                    if (current.isObject()) {
                        if (current.has(condition)) {
                            JsonNode o = current.get(condition);
                            if (o.isArray()) {
                                for (JsonNode item : o) {
                                    result.add(item);
                                }
                            } else {
                                result.add(o);
                            }
                        }
                    }
                }
                return result;
            }
        } else {
            if (!node.has(condition)) {
                throw new InvalidPathException("invalid path");
            } else {
                return node.get(condition);
            }
        }
    }

    public JsonNode filter(JsonNode node) {
        if (node.isArray()) {
            return node;
        } else {
            return node.get(condition);
        }
    }

    @Override
    public JsonNode getRef(JsonNode node) {
        return filter(node);
    }

    @Override
    public boolean isArrayFilter() {
        return false;
    }

}
