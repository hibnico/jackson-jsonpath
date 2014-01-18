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
        return parser.readExpr();
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
            buffer.skip();
            JsonPathExpression onTrueExpr = readExpr();
            buffer.skipWhiteSpace();
            buffer.readExpected(':', "ternary expression");
            JsonPathExpression onFalseExpr = readExpr();
            expr = new TernaryJPE(expr, onTrueExpr, onFalseExpr);
        }

        return expr;
    }

    private JsonPathExpression readConditionnalInclusiveOrExpr() throws ParseException {
        JsonPathExpression expr = readConditionnalAndExpr();

        buffer.skipWhiteSpace();
        Character c1 = buffer.readAhead();
        Character c2 = buffer.readAhead(2);
        if (c1 != null && c2 != null && c1 == '|' && c2 == '|') {
            buffer.skip(2);
            JsonPathExpression rightExpr = readConditionnalAndExpr();
            expr = new BooleanJPE(BooleanOp.OR, expr, rightExpr);
        }

        return expr;
    }

    private JsonPathExpression readConditionnalAndExpr() throws ParseException {
        JsonPathExpression expr = readInclusiveOrExpr();

        buffer.skipWhiteSpace();
        Character c1 = buffer.readAhead();
        Character c2 = buffer.readAhead(2);
        if (c1 != null && c2 != null && c1 == '&' && c2 == '&') {
            buffer.skip(2);
            JsonPathExpression rightExpr = readInclusiveOrExpr();
            expr = new BooleanJPE(BooleanOp.AND, expr, rightExpr);
        }

        return expr;
    }

    private JsonPathExpression readInclusiveOrExpr() throws ParseException {
        JsonPathExpression expr = readExclusiveOrExpr();

        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        if (c != null && c == '|') {
            buffer.skip();
            JsonPathExpression rightExpr = readExclusiveOrExpr();
            expr = new BitwiseJPE(BitwiseOp.OR, expr, rightExpr);
        }

        return expr;
    }

    private JsonPathExpression readExclusiveOrExpr() throws ParseException {
        JsonPathExpression expr = readAndExpr();

        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        if (c != null && c == '^') {
            buffer.skip();
            JsonPathExpression rightExpr = readAndExpr();
            expr = new BitwiseJPE(BitwiseOp.XOR, expr, rightExpr);
        }

        return expr;
    }

    private JsonPathExpression readAndExpr() throws ParseException {
        JsonPathExpression expr = readEqualityExpr();

        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        if (c != null && c == '&') {
            buffer.skip();
            JsonPathExpression rightExpr = readEqualityExpr();
            expr = new BitwiseJPE(BitwiseOp.AND, expr, rightExpr);
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
                buffer.skip(2);
                JsonPathExpression rightExpr = readRelationalExpr();
                expr = new CompareJPE(CompareOp.EQ, expr, rightExpr);
            } else if (c1 == '!' && c2 == '=') {
                buffer.skip(2);
                JsonPathExpression rightExpr = readRelationalExpr();
                expr = new CompareJPE(CompareOp.NE, expr, rightExpr);
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
                buffer.skip(2);
                JsonPathExpression rightExpr = readShiftExpr();
                expr = new CompareJPE(CompareOp.LE, expr, rightExpr);
            } else if (c1 == '<') {
                buffer.skip();
                JsonPathExpression rightExpr = readShiftExpr();
                expr = new CompareJPE(CompareOp.LT, expr, rightExpr);
            } else if (c1 == '>' && c2 == '=') {
                buffer.skip(2);
                JsonPathExpression rightExpr = readShiftExpr();
                expr = new CompareJPE(CompareOp.GE, expr, rightExpr);
            } else if (c1 == '>') {
                buffer.skip();
                JsonPathExpression rightExpr = readShiftExpr();
                expr = new CompareJPE(CompareOp.GT, expr, rightExpr);
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
                buffer.skip(2);
                JsonPathExpression rightExpr = readAdditiveExpr();
                expr = new ShiftJPE(ShiftOp.LEFT, expr, rightExpr);
            } else if (c3 != null && c1 == '>' && c2 == '>' && c3 == '>') {
                buffer.skip(3);
                JsonPathExpression rightExpr = readAdditiveExpr();
                expr = new ShiftJPE(ShiftOp.LOGICAL_RIGHT, expr, rightExpr);
            } else if (c1 == '>' && c2 == '>') {
                buffer.skip(2);
                JsonPathExpression rightExpr = readAdditiveExpr();
                expr = new ShiftJPE(ShiftOp.RIGHT, expr, rightExpr);
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
                buffer.skip();
                JsonPathExpression rightExpr = readMultiplicativeExpr();
                expr = new ArithmeticJPE(ArithmeticOp.PLUS, expr, rightExpr);
            } else if (c == '-') {
                buffer.skip();
                JsonPathExpression rightExpr = readMultiplicativeExpr();
                expr = new ArithmeticJPE(ArithmeticOp.MINUS, expr, rightExpr);
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
                buffer.skip();
                JsonPathExpression rightExpr = readUnaryExpr();
                expr = new ArithmeticJPE(ArithmeticOp.MULT, expr, rightExpr);
            } else if (c == '/') {
                buffer.skip();
                JsonPathExpression rightExpr = readUnaryExpr();
                expr = new ArithmeticJPE(ArithmeticOp.DIV, expr, rightExpr);
            } else if (c == '%') {
                buffer.skip();
                JsonPathExpression rightExpr = readUnaryExpr();
                expr = new ArithmeticJPE(ArithmeticOp.MODULO, expr, rightExpr);
            }
        }

        return expr;
    }

    private JsonPathExpression readUnaryExpr() throws ParseException {
        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        JsonPathExpression expr;
        if (c != null && c == '+') {
            buffer.skip();
            expr = new UnaryJPE(UnaryOp.PLUS, readUnaryExpr());
        } else if (c != null && c == '-') {
            buffer.skip();
            expr = new UnaryJPE(UnaryOp.MINUS, readUnaryExpr());
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
            buffer.skip();
            expr = new UnaryJPE(UnaryOp.NOT_BITWISE, readUnaryExpr());
        } else if (c != null && c == '!') {
            buffer.skip();
            expr = new UnaryJPE(UnaryOp.NOT, readUnaryExpr());
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
            buffer.skip();
            expr = new RootJPE();
        } else if (c != null && c == '@') {
            buffer.skip();
            expr = new ThisJPE();
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
            buffer.skip();
            String value = readEscaped('\'');
            buffer.readExpected('\'', "end of string \'");
            return new LiteralJPE(JsonNodeFactory.instance.textNode(value));
        }
        if (c == '"') {
            buffer.skip();
            String value = readEscaped('"');
            buffer.readExpected('"', "end of string \"");
            return new LiteralJPE(JsonNodeFactory.instance.textNode(value));
        }
        Character c2 = buffer.readAhead(2);
        Character c3 = buffer.readAhead(2);
        if (c == '0' && (c2 == 'x' || c2 == 'X')
                && (c3 != null && (c3 >= '0' && c3 <= '9') || (c3 >= 'A' && c3 < 'F') || (c3 >= 'a' && c3 < 'f'))) {
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
            return new LiteralJPE(JsonNodeFactory.instance.numberNode(Long.parseLong(numberBuffer.toString())));
        } else if (c == '0' && (c2 == 'b' || c2 == 'B') && (c3 == '0' || c3 == '1')) {
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
            return new LiteralJPE(JsonNodeFactory.instance.numberNode(Long.parseLong(numberBuffer.toString())));
        } else if (c == '0' && c2 != null && c2 >= '0' && c2 <= '7') {
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
            return new LiteralJPE(JsonNodeFactory.instance.numberNode(Long.parseLong(numberBuffer.toString())));
        } else if (c >= '0' && c <= '9') {
            StringBuilder numberBuffer = startReadNumber();
            if (c == '.') {
                appendFloatingPoint(numberBuffer);
                return new LiteralJPE(JsonNodeFactory.instance.numberNode(Double.parseDouble(numberBuffer.toString())));
            }
            if (c == 'f' || c == 'F' || c == 'd' || c == 'D') {
                numberBuffer.append(c);
                buffer.skip();
                return new LiteralJPE(JsonNodeFactory.instance.numberNode(Double.parseDouble(numberBuffer.toString())));
            }
            if (c == 'l' || c == 'L') {
                numberBuffer.append(c);
                buffer.skip();
            }
            return new LiteralJPE(JsonNodeFactory.instance.numberNode(Long.parseLong(numberBuffer.toString())));
        } else if (c == '.' && c2 != null && c2 >= '0' && c2 <= '9') {
            // double number
            StringBuilder numberBuffer = new StringBuilder();
            appendFloatingPoint(numberBuffer);
            return new LiteralJPE(JsonNodeFactory.instance.numberNode(Double.parseDouble(numberBuffer.toString())));
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
            buffer.skip(2);
            expr = new DescendingJPE(expr);
        } else if (c != null && c2 != null && c == '.' && c2 == '*') {
            buffer.skip(2);
            expr = new WildcardJPE(expr);
        } else if (c != null && c == '.') {
            buffer.skip();
            buffer.skipWhiteSpace();
            c = buffer.readAhead();
            if (c != null && c >= '0' && c <= '9') {
                int i = readInt();
                expr = new SelectorJPE(expr, i);
            } else {
                String id = readIdentifier();
                List<JsonPathExpression> arguments = readArguments();
                if (arguments != null) {
                    expr = new MethodCallJPE(expr, id, arguments);
                } else {
                    expr = new SelectorJPE(expr, id);
                }
            }
        } else if (c != null && c == '[') {
            buffer.skip();
            buffer.skipWhiteSpace();
            c = buffer.readAhead();
            if (c != null && c == '*') {
                buffer.skip();
                expr = new WildcardSelectorJPE(expr);
            } else if (c != null && c2 != null && c == '?' && c2 == '(') {
                buffer.skip(2);
                JsonPathExpression filter = readExpr();
                expr = new FilterJPE(expr, filter);
                buffer.readExpected(')', "end of filter");
            } else if (c != null && c == '(') {
                buffer.skip();
                JsonPathExpression indexExpr = readExpr();
                expr = new SelectorJPE(expr, indexExpr);
                buffer.skipWhiteSpace();
                buffer.readExpected(')', "end of expression");
            } else if (c != null && c == '\'') {
                buffer.skip();
                String field = readEscaped('\'');
                expr = new SelectorJPE(expr, field);
                buffer.readExpected('\'', "end of string");
            } else if (c != null && c == '\"') {
                buffer.skip();
                String field = readEscaped('\"');
                expr = new SelectorJPE(expr, field);
                buffer.readExpected('\"', "end of string");
            } else if (c != null && c >= '0' && c <= '9') {
                int i = readInt();
                expr = new SelectorJPE(expr, i);
            } else {
                String field = readIdentifier();
                expr = new SelectorJPE(expr, field);
            }
            buffer.skipWhiteSpace();
            buffer.readExpected(']', "array selector");
        } else {
            return expr;
        }
        readSelectorExpr(expr);
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

    private String readIdentifier() {
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
            throw new IllegalStateException("Expecting an identifier");
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
