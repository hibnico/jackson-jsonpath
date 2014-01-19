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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.jsonpath.internal.ArithmeticJPE.ArithmeticOp;
import com.fasterxml.jackson.jsonpath.internal.BitwiseJPE.BitwiseOp;
import com.fasterxml.jackson.jsonpath.internal.BooleanJPE.BooleanOp;
import com.fasterxml.jackson.jsonpath.internal.CompareJPE.CompareOp;
import com.fasterxml.jackson.jsonpath.internal.ShiftJPE.ShiftOp;
import com.fasterxml.jackson.jsonpath.internal.UnaryJPE.UnaryOp;

public class JsonPathExpressionParser {

    private Buffer buffer;

    public static JsonPathExpression parse(String path) throws ParseException {
        JsonPathExpressionParser parser = new JsonPathExpressionParser(new Buffer(path));
        JsonPathExpression expr = parser.readExpr();
        parser.buffer.skipWhiteSpace();
        if (!parser.buffer.isConsumed()) {
            throw new ParseException("Expected character at end of expression", parser.buffer.pos + 1);
        }
        return expr;
    }

    public JsonPathExpressionParser(Buffer buffer) {
        this.buffer = buffer;
    }

    private JsonPathExpression readExpr() throws ParseException {
        return readTernaryExpr();
    }

    private JsonPathExpression readTernaryExpr() throws ParseException {
        JsonPathExpression expr = readConditionnalInclusiveOrExpr();

        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        if (c != null && c == '?') {
            int p = buffer.pos;
            buffer.skip();
            JsonPathExpression onTrueExpr = readExpr();
            buffer.skipWhiteSpace();
            buffer.readExpected(':', "ternary expression");
            JsonPathExpression onFalseExpr = readExpr();
            expr = new TernaryJPE(p, expr, onTrueExpr, onFalseExpr);
        }

        return expr;
    }

    private JsonPathExpression readConditionnalInclusiveOrExpr() throws ParseException {
        JsonPathExpression expr = readConditionnalAndExpr();

        buffer.skipWhiteSpace();
        Character c1 = buffer.readAhead();
        Character c2 = buffer.readAhead(2);
        if (c1 != null && c2 != null && c1 == '|' && c2 == '|') {
            int p = buffer.pos;
            buffer.skip(2);
            JsonPathExpression rightExpr = readConditionnalInclusiveOrExpr();
            expr = new BooleanJPE(p, BooleanOp.OR, expr, rightExpr);
        }

        return expr;
    }

    private JsonPathExpression readConditionnalAndExpr() throws ParseException {
        JsonPathExpression expr = readInclusiveOrExpr();

        buffer.skipWhiteSpace();
        Character c1 = buffer.readAhead();
        Character c2 = buffer.readAhead(2);
        if (c1 != null && c2 != null && c1 == '&' && c2 == '&') {
            int p = buffer.pos;
            buffer.skip(2);
            JsonPathExpression rightExpr = readConditionnalAndExpr();
            expr = new BooleanJPE(p, BooleanOp.AND, expr, rightExpr);
        }

        return expr;
    }

    private JsonPathExpression readInclusiveOrExpr() throws ParseException {
        JsonPathExpression expr = readExclusiveOrExpr();

        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        Character c2 = buffer.readAhead(2);
        if (c != null && c == '|' && (c2 == null || c2 != '|')) {
            int p = buffer.pos;
            buffer.skip();
            JsonPathExpression rightExpr = readInclusiveOrExpr();
            expr = new BitwiseJPE(p, BitwiseOp.OR, expr, rightExpr);
        }

        return expr;
    }

    private JsonPathExpression readExclusiveOrExpr() throws ParseException {
        JsonPathExpression expr = readAndExpr();

        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        Character c2 = buffer.readAhead(2);
        if (c != null && c == '^' && (c2 == null || c2 != '^')) {
            int p = buffer.pos;
            buffer.skip();
            JsonPathExpression rightExpr = readExclusiveOrExpr();
            expr = new BitwiseJPE(p, BitwiseOp.XOR, expr, rightExpr);
        }

        return expr;
    }

    private JsonPathExpression readAndExpr() throws ParseException {
        JsonPathExpression expr = readEqualityExpr();

        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        Character c2 = buffer.readAhead(2);
        if (c != null && c == '&' && (c2 == null || c2 != '&')) {
            int p = buffer.pos;
            buffer.skip();
            JsonPathExpression rightExpr = readAndExpr();
            expr = new BitwiseJPE(p, BitwiseOp.AND, expr, rightExpr);
        }

        return expr;
    }

    private JsonPathExpression readEqualityExpr() throws ParseException {
        JsonPathExpression expr = readRelationalExpr();

        buffer.skipWhiteSpace();
        Character c1 = buffer.readAhead();
        Character c2 = buffer.readAhead(2);
        if (c1 != null && c2 != null) {
            if (c1 == '=' && c2 == '=') {
                int p = buffer.pos;
                buffer.skip(2);
                JsonPathExpression rightExpr = readEqualityExpr();
                expr = new CompareJPE(p, CompareOp.EQ, expr, rightExpr);
            } else if (c1 == '!' && c2 == '=') {
                int p = buffer.pos;
                buffer.skip(2);
                JsonPathExpression rightExpr = readEqualityExpr();
                expr = new CompareJPE(p, CompareOp.NE, expr, rightExpr);
            }
        }

        return expr;
    }

    private JsonPathExpression readRelationalExpr() throws ParseException {
        JsonPathExpression expr = readShiftExpr();

        buffer.skipWhiteSpace();
        Character c1 = buffer.readAhead();
        Character c2 = buffer.readAhead(2);
        if (c1 != null && c2 != null) {
            if (c1 == '<' && c2 == '=') {
                int p = buffer.pos;
                buffer.skip(2);
                JsonPathExpression rightExpr = readRelationalExpr();
                expr = new CompareJPE(p, CompareOp.LE, expr, rightExpr);
            } else if (c1 == '<') {
                int p = buffer.pos;
                buffer.skip();
                JsonPathExpression rightExpr = readRelationalExpr();
                expr = new CompareJPE(p, CompareOp.LT, expr, rightExpr);
            } else if (c1 == '>' && c2 == '=') {
                int p = buffer.pos;
                buffer.skip(2);
                JsonPathExpression rightExpr = readRelationalExpr();
                expr = new CompareJPE(p, CompareOp.GE, expr, rightExpr);
            } else if (c1 == '>') {
                int p = buffer.pos;
                buffer.skip();
                JsonPathExpression rightExpr = readRelationalExpr();
                expr = new CompareJPE(p, CompareOp.GT, expr, rightExpr);
            }
        }

        return expr;
    }

    private JsonPathExpression readShiftExpr() throws ParseException {
        JsonPathExpression expr = readAdditiveExpr();

        buffer.skipWhiteSpace();
        Character c1 = buffer.readAhead();
        Character c2 = buffer.readAhead(2);
        Character c3 = buffer.readAhead(3);
        if (c1 != null && c2 != null) {
            if (c1 == '<' && c2 == '<') {
                int p = buffer.pos;
                buffer.skip(2);
                JsonPathExpression rightExpr = readShiftExpr();
                expr = new ShiftJPE(p, ShiftOp.LEFT, expr, rightExpr);
            } else if (c3 != null && c1 == '>' && c2 == '>' && c3 == '>') {
                int p = buffer.pos;
                buffer.skip(3);
                JsonPathExpression rightExpr = readShiftExpr();
                expr = new ShiftJPE(p, ShiftOp.LOGICAL_RIGHT, expr, rightExpr);
            } else if (c1 == '>' && c2 == '>') {
                int p = buffer.pos;
                buffer.skip(2);
                JsonPathExpression rightExpr = readShiftExpr();
                expr = new ShiftJPE(p, ShiftOp.RIGHT, expr, rightExpr);
            }
        }

        return expr;
    }

    private JsonPathExpression readAdditiveExpr() throws ParseException {
        JsonPathExpression expr = readMultiplicativeExpr();

        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        if (c != null) {
            if (c == '+') {
                int p = buffer.pos;
                buffer.skip();
                JsonPathExpression rightExpr = readAdditiveExpr();
                expr = new ArithmeticJPE(p, ArithmeticOp.PLUS, expr, rightExpr);
            } else if (c == '-') {
                int p = buffer.pos;
                buffer.skip();
                JsonPathExpression rightExpr = readAdditiveExpr();
                expr = new ArithmeticJPE(p, ArithmeticOp.MINUS, expr, rightExpr);
            }
        }

        return expr;
    }

    private JsonPathExpression readMultiplicativeExpr() throws ParseException {
        JsonPathExpression expr = readUnaryExpr();

        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        if (c != null) {
            if (c == '*') {
                int p = buffer.pos;
                buffer.skip();
                JsonPathExpression rightExpr = readMultiplicativeExpr();
                expr = new ArithmeticJPE(p, ArithmeticOp.MULT, expr, rightExpr);
            } else if (c == '/') {
                int p = buffer.pos;
                buffer.skip();
                JsonPathExpression rightExpr = readMultiplicativeExpr();
                expr = new ArithmeticJPE(p, ArithmeticOp.DIV, expr, rightExpr);
            } else if (c == '%') {
                int p = buffer.pos;
                buffer.skip();
                JsonPathExpression rightExpr = readMultiplicativeExpr();
                expr = new ArithmeticJPE(p, ArithmeticOp.MODULO, expr, rightExpr);
            }
        }

        return expr;
    }

    private JsonPathExpression readUnaryExpr() throws ParseException {
        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        JsonPathExpression expr;
        if (c != null && c == '+') {
            int p = buffer.pos;
            buffer.skip();
            expr = new UnaryJPE(p, UnaryOp.PLUS, readUnaryExpr());
        } else if (c != null && c == '-') {
            int p = buffer.pos;
            buffer.skip();
            expr = new UnaryJPE(p, UnaryOp.MINUS, readUnaryExpr());
        } else {
            expr = readUnaryExprNotPlusMinus();
        }
        return expr;
    }

    private JsonPathExpression readUnaryExprNotPlusMinus() throws ParseException {
        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        JsonPathExpression expr;
        if (c != null && c == '~') {
            int p = buffer.pos;
            buffer.skip();
            expr = new UnaryJPE(p, UnaryOp.NOT_BITWISE, readUnaryExpr());
        } else if (c != null && c == '!') {
            int p = buffer.pos;
            buffer.skip();
            expr = new UnaryJPE(p, UnaryOp.NOT, readUnaryExpr());
        } else {
            expr = readPrimaryExpr();
            expr = readSelectorExpr(expr);
        }
        return expr;
    }

    private JsonPathExpression readPrimaryExpr() throws ParseException {
        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        JsonPathExpression expr;
        if (c != null && c == '(') {
            buffer.skip();
            expr = readExpr();
            buffer.skipWhiteSpace();
            buffer.readExpected(')', "parenthesed expression");
        } else if (c != null && c == '$') {
            int p = buffer.pos;
            buffer.skip();
            expr = new RootJPE(p);
        } else if (c != null && c == '@') {
            int p = buffer.pos;
            buffer.skip();
            expr = new ThisJPE(p);
        } else {
            expr = readLiteral();
        }

        return expr;
    }

    private JsonPathExpression readLiteral() throws ParseException {
        Character c = buffer.readAhead();
        if (c == null) {
            return null;
        }
        if (c == '\'') {
            int p = buffer.pos;
            buffer.skip();
            String value = readEscaped('\'');
            buffer.readExpected('\'', "end of string \'");
            return new LiteralJPE(p, JsonNodeFactory.instance.textNode(value));
        }
        if (c == '"') {
            int p = buffer.pos;
            buffer.skip();
            String value = readEscaped('"');
            buffer.readExpected('"', "end of string \"");
            return new LiteralJPE(p, JsonNodeFactory.instance.textNode(value));
        }
        Character c2 = buffer.readAhead(2);
        Character c3 = buffer.readAhead(3);
        if (c == '0' && (c2 == 'x' || c2 == 'X')
                && (c3 != null && (c3 >= '0' && c3 <= '9') || (c3 >= 'A' && c3 < 'F') || (c3 >= 'a' && c3 < 'f'))) {
            int p = buffer.pos;
            // hexa number
            StringBuilder numberBuffer = new StringBuilder();
            numberBuffer.append(c);
            numberBuffer.append(c2);
            buffer.skip(2);
            c = buffer.readAhead();
            while (c != null) {
                if (c >= '0' && c <= '9' || c >= 'A' && c < 'F' || c >= 'a' && c < 'f' || c == '_') {
                    numberBuffer.append(c);
                } else {
                    break;
                }
                buffer.skip();
                c = buffer.readAhead();
            }
            if (c == 'l' || c == 'L') {
                numberBuffer.append(c);
                buffer.skip();
            }
            return new LiteralJPE(p, JsonNodeFactory.instance.numberNode(Long.parseLong(numberBuffer.toString())));
        }
        if (c == '0' && (c2 == 'b' || c2 == 'B') && (c3 == '0' || c3 == '1')) {
            int p = buffer.pos;
            // binary number
            StringBuilder numberBuffer = new StringBuilder();
            numberBuffer.append(c);
            numberBuffer.append(c2);
            buffer.skip(2);
            c = buffer.readAhead();
            while (c != null) {
                if (c == '0' || c == '1' || c == '_') {
                    numberBuffer.append(c);
                } else {
                    break;
                }
                buffer.skip();
                c = buffer.readAhead();
            }
            if (c == 'l' || c == 'L') {
                numberBuffer.append(c);
                buffer.skip();
            }
            return new LiteralJPE(p, JsonNodeFactory.instance.numberNode(Long.parseLong(numberBuffer.toString())));
        }
        if (c == '0' && c2 != null && c2 >= '0' && c2 <= '7') {
            int p = buffer.pos;
            // octal number
            StringBuilder numberBuffer = new StringBuilder();
            numberBuffer.append(c);
            buffer.skip();
            c = buffer.readAhead();
            while (c != null) {
                if (c >= '0' && c <= '7' || c == '_') {
                    numberBuffer.append(c);
                } else {
                    break;
                }
                buffer.skip();
                c = buffer.readAhead();
            }
            if (c == 'l' || c == 'L') {
                numberBuffer.append(c);
                buffer.skip();
            }
            return new LiteralJPE(p, JsonNodeFactory.instance.numberNode(Long.parseLong(numberBuffer.toString())));
        }
        if (c >= '0' && c <= '9') {
            int p = buffer.pos;
            StringBuilder numberBuffer = startReadNumber();
            if (c == '.') {
                appendFloatingPoint(numberBuffer);
                return new LiteralJPE(p,
                        JsonNodeFactory.instance.numberNode(Double.parseDouble(numberBuffer.toString())));
            }
            if (c == 'f' || c == 'F' || c == 'd' || c == 'D') {
                numberBuffer.append(c);
                buffer.skip();
                return new LiteralJPE(p,
                        JsonNodeFactory.instance.numberNode(Double.parseDouble(numberBuffer.toString())));
            }
            if (c == 'l' || c == 'L') {
                numberBuffer.append(c);
                buffer.skip();
            }
            return new LiteralJPE(p, JsonNodeFactory.instance.numberNode(Long.parseLong(numberBuffer.toString())));
        }
        if (c == '.' && c2 != null && c2 >= '0' && c2 <= '9') {
            int p = buffer.pos;
            // double number
            StringBuilder numberBuffer = new StringBuilder();
            appendFloatingPoint(numberBuffer);
            return new LiteralJPE(p, JsonNodeFactory.instance.numberNode(Double.parseDouble(numberBuffer.toString())));
        }
        Character c4 = buffer.readAhead(4);
        if (c != null && c2 != null && c3 != null && c4 != null && c == 'n' && c2 == 'u' && c3 == 'l' && c4 == 'l') {
            int p = buffer.pos;
            buffer.skip(4);
            return new LiteralJPE(p, JsonNodeFactory.instance.nullNode());
        }
        return null;
    }

    private String readEscaped(char end) {
        StringBuilder s = new StringBuilder();
        Character c = null;
        while ((c = buffer.readAhead()) != null) {
            if (c == end) {
                break;
            }
            buffer.skip();
            if (c == '\\') {
                c = buffer.read();
                if (c == null) {
                    break;
                }
                s.append(c);
            } else {
                s.append(c);
            }
        }
        return s.toString();
    }

    private void appendFloatingPoint(StringBuilder numberBuffer) {
        numberBuffer.append(buffer.read()); // adding the dot
        Character c = buffer.readAhead();
        while (c != null) {
            if (c >= '0' && c <= '9' || c == '_') {
                numberBuffer.append(c);
            } else {
                break;
            }
            buffer.skip();
            c = buffer.readAhead();
        }
        Character c2 = buffer.readAhead(2);
        Character c3 = buffer.readAhead(3);
        if (c != null
                && (c == 'E' || c == 'e')
                && ((c2 == '+' || c2 == '-') && (c3 != null && c3 >= '0' && c3 <= '9') || (c2 != null && c2 >= '0' && c2 <= '9'))) {
            numberBuffer.append(c);
            buffer.skip();
            if (c2 == '+' || c2 == '-') {
                numberBuffer.append(c);
                buffer.skip();
                c = c3;
            } else {
                c = c2;
            }
            while (c != null) {
                if (c >= '0' && c <= '9') {
                    numberBuffer.append(c);
                } else {
                    break;
                }
                buffer.skip();
                c = buffer.readAhead();
            }
        }
        c = buffer.readAhead();
        if (c != null && (c == 'f' || c == 'F' || c == 'd' || c == 'D')) {
            numberBuffer.append(c);
            buffer.skip();
        }
    }

    private JsonPathExpression readSelectorExpr(JsonPathExpression expr) throws ParseException {
        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        Character c2 = buffer.readAhead(2);
        if (c != null && c2 != null && c == '.' && c2 == '.') {
            int p = buffer.pos;
            buffer.skip(2);
            expr = new DescendingJPE(p, expr);
        } else if (c != null && c2 != null && c == '.' && c2 == '*') {
            int p = buffer.pos;
            buffer.skip(2);
            expr = new WildcardJPE(p, expr);
        } else if (c != null && c == '.') {
            int p = buffer.pos;
            buffer.skip();
            buffer.skipWhiteSpace();
            c = buffer.readAhead();
            if (c != null && c >= '0' && c <= '9') {
                int i = readInt();
                expr = new SelectorJPE(p, expr, i);
            } else {
                String id = readIdentifier();
                List<JsonPathExpression> arguments = readArguments();
                if (arguments != null) {
                    expr = new MethodCallJPE(p, expr, id, arguments);
                } else {
                    expr = new SelectorJPE(p, expr, id);
                }
            }
        } else if (c != null && c == '[') {
            buffer.skip();
            buffer.skipWhiteSpace();
            c = buffer.readAhead();
            c2 = buffer.readAhead(2);
            if (c != null && c == '*') {
                int p = buffer.pos;
                buffer.skip();
                expr = new WildcardSelectorJPE(p, expr);
            } else if (c != null && c2 != null && c == '?' && c2 == '(') {
                int p = buffer.pos;
                buffer.skip(2);
                JsonPathExpression filter = readExpr();
                expr = new FilterJPE(p, expr, filter);
                buffer.readExpected(')', "filter");
            } else if (c != null && c == '(') {
                int p = buffer.pos;
                buffer.skip();
                JsonPathExpression indexExpr = readExpr();
                expr = new SelectorJPE(p, expr, indexExpr);
                buffer.skipWhiteSpace();
                buffer.readExpected(')', "expression");
            } else if (c != null && c == '\'') {
                int p = buffer.pos;
                buffer.skip();
                String field = readEscaped('\'');
                expr = new SelectorJPE(p, expr, field);
                buffer.readExpected('\'', "string");
            } else if (c != null && c == '\"') {
                int p = buffer.pos;
                buffer.skip();
                String field = readEscaped('\"');
                expr = new SelectorJPE(p, expr, field);
                buffer.readExpected('\"', "string");
            } else if (c != null && c >= '0' && c <= '9') {
                int p = buffer.pos;
                int i = readInt();
                expr = new SelectorJPE(p, expr, i);
            } else {
                int p = buffer.pos;
                String field = readIdentifier();
                expr = new SelectorJPE(p, expr, field);
            }
            buffer.skipWhiteSpace();
            buffer.readExpected(']', "array selector");
        } else {
            return expr;
        }
        expr = readSelectorExpr(expr);
        return expr;
    }

    private StringBuilder startReadNumber() {
        StringBuilder numberBuffer = new StringBuilder();
        Character c = null;
        while ((c = buffer.readAhead()) != null) {
            if (c >= '0' && c <= '9' || c == '_') {
                numberBuffer.append(c);
            } else {
                break;
            }
            buffer.skip();
            c = buffer.readAhead();
        }
        return numberBuffer;
    }

    private int readInt() {
        StringBuilder numberBuffer = startReadNumber();
        return Integer.parseInt(numberBuffer.toString());
    }

    private String readIdentifier() throws ParseException {
        StringBuilder name = new StringBuilder();
        Character c = null;
        while ((c = buffer.readAhead()) != null) {
            if (!(isAlpha(c) || name.length() > 0 && isNum(c))) {
                break;
            }
            buffer.skip();
            name.append(c);
        }
        if (name.length() == 0) {
            throw new ParseException("expecting an identifier", buffer.pos);
        }
        return name.toString();
    }

    private boolean isAlpha(Character c) {
        return c != null && (c == '_' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z');
    }

    private boolean isNum(Character c) {
        return c != null && (c >= '0' && c <= '9');
    }

    private List<JsonPathExpression> readArguments() throws ParseException {
        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        List<JsonPathExpression> arguments;
        if (c != null && c == '(') {
            arguments = new ArrayList<JsonPathExpression>();
            do {
                buffer.skip();
                JsonPathExpression argument = readExpr();
                arguments.add(argument);
                buffer.skipWhiteSpace();
            } while (buffer.readAhead() == ',');
        } else {
            arguments = null;
        }
        return arguments;
    }

}
