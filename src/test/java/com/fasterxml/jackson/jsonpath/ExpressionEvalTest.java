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
package com.fasterxml.jackson.jsonpath;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.jsonpath.internal.filter.eval.ExpressionEvaluator;

public class ExpressionEvalTest {

    private void checkNumber(int i, String op, String value, boolean expected) {
        assertEquals(ExpressionEvaluator.eval(JsonNodeFactory.instance.numberNode(i), op, value), expected);
    }

    private void checkNumber(long l, String op, String value, boolean expected) {
        assertEquals(ExpressionEvaluator.eval(JsonNodeFactory.instance.numberNode(l), op, value), expected);
    }

    private void checkNumber(double d, String op, String value, boolean expected) {
        assertEquals(ExpressionEvaluator.eval(JsonNodeFactory.instance.numberNode(d), op, value), expected);
    }

    private void checkString(String s, String op, String value, boolean expected) {
        assertEquals(ExpressionEvaluator.eval(JsonNodeFactory.instance.textNode(s), op, value), expected);
    }

    @Test
    public void long_eval() throws Exception {
        checkNumber(1L, "==", "1", true);
        checkNumber(2L, "!=", "1", true);
        checkNumber(2L, ">", "1", true);
        checkNumber(2L, ">=", "1", true);
        checkNumber(2L, ">=", "2", true);
        checkNumber(1L, "<", "2", true);
        checkNumber(2L, "<=", "2", true);

        checkNumber(1, ">", "2", false);
        checkNumber(1, ">=", "2", false);
        checkNumber(2, "<", "1", false);
        checkNumber(2, "<=", "1", false);
        checkNumber(1, "==", "2", false);
        checkNumber(1, "!=", "1", false);
    }

    @Test
    public void double_eval() throws Exception {
        checkNumber(1D, "==", "1", true);
        checkNumber(2D, "!=", "1", true);
        checkNumber(2D, ">", "1", true);
        checkNumber(2D, ">=", "1", true);
        checkNumber(2D, ">=", "2", true);
        checkNumber(1D, "<", "2", true);
        checkNumber(2D, "<=", "2", true);

        checkNumber(1D, ">", "2", false);
        checkNumber(1D, ">=", "2", false);
        checkNumber(2D, "<", "1", false);
        checkNumber(2D, "<=", "1", false);
        checkNumber(1D, "==", "2", false);
        checkNumber(1D, "!=", "1", false);
    }

    @Test
    public void string_eval() throws Exception {
        checkString("A", "==", "A", true);
        checkString("B", "!=", "A", true);

    }

}
