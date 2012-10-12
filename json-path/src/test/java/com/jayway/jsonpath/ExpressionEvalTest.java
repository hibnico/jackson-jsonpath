package com.jayway.jsonpath;

import static org.junit.Assert.assertEquals;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.junit.Test;

import com.jayway.jsonpath.internal.filter.eval.ExpressionEvaluator;

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
