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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.jsonpath.InvalidPathException;
import com.fasterxml.jackson.jsonpath.internal.filter.eval.ExpressionEvaluator;

public class ArrayEvalFilter extends PathTokenFilter {

    private static final Pattern PATTERN = Pattern.compile("(.*?)\\s?([=<>]+)\\s?(.*)");

    public ArrayEvalFilter(String condition) {
        super(condition);
    }

    @Override
    public JsonNode filter(JsonNode node) {
        // [?(@.isbn == 10)]
        ArrayNode result = JsonNodeFactory.instance.arrayNode();

        String trimmedCondition = condition;

        if (condition.contains("['")) {
            trimmedCondition = trimmedCondition.replace("['", ".");
            trimmedCondition = trimmedCondition.replace("']", "");
        }

        trimmedCondition = trim(trimmedCondition, 5, 2);

        ConditionStatement conditionStatement = createConditionStatement(trimmedCondition);

        for (JsonNode item : node) {
            if (isMatch(item, conditionStatement)) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public JsonNode getRef(JsonNode node) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public boolean isArrayFilter() {
        return true;
    }

    private boolean isMatch(JsonNode node, ConditionStatement conditionStatement) {
        if (!node.isObject()) {
            return false;
        }

        if (!node.has(conditionStatement.getField())) {
            return false;
        }

        JsonNode propertyValue = node.get(conditionStatement.getField());

        if (propertyValue.isContainerNode()) {
            return false;
        }
        return ExpressionEvaluator.eval(propertyValue, conditionStatement.getOperator(), conditionStatement.getExpected());
    }

    private ConditionStatement createConditionStatement(String str) {
        Matcher matcher = PATTERN.matcher(str);
        if (matcher.matches()) {
            String property = matcher.group(1);
            String operator = matcher.group(2);
            String expected = matcher.group(3);

            return new ConditionStatement(property, operator, expected);
        } else {
            throw new InvalidPathException("Invalid match " + str);
        }
    }

    private class ConditionStatement {
        private final String field;
        private final String operator;
        private String expected;

        private ConditionStatement(String field, String operator, String expected) {
            this.field = field;
            this.operator = operator.trim();
            this.expected = expected;

            if (this.expected.startsWith("'")) {
                this.expected = trim(this.expected, 1, 1);
            }
        }

        public String getField() {
            return field;
        }

        public String getOperator() {
            return operator;
        }

        public String getExpected() {
            return expected;
        }
    }
}
