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

import java.util.regex.Pattern;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;

/**
 * @author Kalle Stenflo
 */
public class ArrayIndexFilter extends PathTokenFilter {

    private static final Pattern SINGLE_ARRAY_INDEX_PATTERN = Pattern.compile("\\[\\d+\\]");

    public ArrayIndexFilter(String condition) {
        super(condition);
    }

    @Override
    public JsonNode filter(JsonNode node) {
        ArrayNode result = JsonNodeFactory.instance.arrayNode();

        String trimmedCondition = trim(condition, 1, 1);

        if (trimmedCondition.contains("@.length")) {
            trimmedCondition = trim(trimmedCondition, 1, 1);
            trimmedCondition = trimmedCondition.replace("@.length", "");
            trimmedCondition = trimmedCondition + ":";
        }

        if (trimmedCondition.startsWith(":")) {
            trimmedCondition = trim(trimmedCondition, 1, 0);
            int get = Integer.parseInt(trimmedCondition);
            for (int i = 0; i < get; i++) {
                result.add(node.get(i));
            }
            return result;

        } else if (trimmedCondition.endsWith(":")) {
            trimmedCondition = trim(trimmedCondition.replace(" ", ""), 1, 1);
            int get = Integer.parseInt(trimmedCondition);
            return node.get(node.size() - get);

        } else {
            String[] indexArr = trimmedCondition.split(",");

            if (node.size() == 0) {
                return result;
            }

            if (indexArr.length == 1) {
                int index = Integer.parseInt(indexArr[0]);
                if (index >= node.size()) {
                    throw new IndexOutOfBoundsException("Accessing item " + index + " of an array of " + node.size() + " elements");
                }
                return node.get(index);

            } else {
                for (String idx : indexArr) {
                    result.add(node.get(Integer.parseInt(idx.trim())));
                }
                return result;
            }
        }
    }

    @Override
    public JsonNode getRef(JsonNode node) {
        if (SINGLE_ARRAY_INDEX_PATTERN.matcher(condition).matches()) {
            String trimmedCondition = trim(condition, 1, 1);
            return node.get(Integer.parseInt(trimmedCondition));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean isArrayFilter() {
        return true;
    }
}
