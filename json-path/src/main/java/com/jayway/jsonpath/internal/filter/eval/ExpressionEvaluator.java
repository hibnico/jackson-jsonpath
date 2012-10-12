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
package com.jayway.jsonpath.internal.filter.eval;

import org.codehaus.jackson.JsonNode;

public class ExpressionEvaluator {

    public static boolean eval(JsonNode actual, String comparator, String expected) {

        if (actual.isLong()) {

            Long a = actual.asLong();
            Long e = Long.parseLong(expected.trim());

            if ("==".equals(comparator)) {
                return a.longValue() == e.longValue();
            } else if ("!=".equals(comparator) || "<>".equals(comparator)) {
                return a.longValue() != e.longValue();
            } else if (">".equals(comparator)) {
                return a > e;
            } else if (">=".equals(comparator)) {
                return a >= e;
            } else if ("<".equals(comparator)) {
                return a < e;
            } else if ("<=".equals(comparator)) {
                return a <= e;
            }
        } else if (actual.isInt()) {
            Integer a = actual.asInt();
            Integer e = Integer.parseInt(expected.trim());

            if ("==".equals(comparator)) {
                return a.intValue() == e.intValue();
            } else if ("!=".equals(comparator) || "<>".equals(comparator)) {
                return a.intValue() != e.intValue();
            } else if (">".equals(comparator)) {
                return a > e;
            } else if (">=".equals(comparator)) {
                return a >= e;
            } else if ("<".equals(comparator)) {
                return a < e;
            } else if ("<=".equals(comparator)) {
                return a <= e;
            }
        } else if (actual.isDouble()) {

            Double a = actual.asDouble();
            Double e = Double.parseDouble(expected.trim());

            if ("==".equals(comparator)) {
                return a.doubleValue() == e.doubleValue();
            } else if ("!=".equals(comparator) || "<>".equals(comparator)) {
                return a.doubleValue() != e.doubleValue();
            } else if (">".equals(comparator)) {
                return a > e;
            } else if (">=".equals(comparator)) {
                return a >= e;
            } else if ("<".equals(comparator)) {
                return a < e;
            } else if ("<=".equals(comparator)) {
                return a <= e;
            }
        } else if (actual.isTextual()) {

            String a = actual.asText();
            expected = expected.trim();
            if (expected.startsWith("'")) {
                expected = expected.substring(1);
            }
            if (expected.endsWith("'")) {
                expected = expected.substring(0, expected.length() - 1);
            }

            if ("==".equals(comparator)) {
                return a.equals(expected);
            } else if ("!=".equals(comparator) || "<>".equals(comparator)) {
                return !a.equals(expected);
            }
        }

        return false;
    }
}
