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
package com.fasterxml.jackson.jsonpath.internal.js;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.jsonpath.internal.Buffer;
import com.fasterxml.jackson.jsonpath.internal.js.ArithmeticJSExpr.ArithmeticOp;
import com.fasterxml.jackson.jsonpath.internal.js.BitwiseJSExpr.BitwiseOp;
import com.fasterxml.jackson.jsonpath.internal.js.BooleanJSExpr.BooleanOp;
import com.fasterxml.jackson.jsonpath.internal.js.CompareJSExpr.CompareOp;
import com.fasterxml.jackson.jsonpath.internal.js.ShiftJSExpr.ShiftOp;
import com.fasterxml.jackson.jsonpath.internal.js.UnaryJSExpr.UnaryOp;

public class JSExprParser {

    private Buffer buffer;

    private JSExpr expression;

    public JSExprParser(Buffer buffer) {
        this.buffer = buffer;
    }

    public JSExpr getExpression() {
        return expression;
    }

    public void parse() throws ParseException {
        expression = readExpr();
    }

    private JSExpr readExpr() throws ParseException {
        return readTernaryExpr();
    }

    private JSExpr readTernaryExpr() throws ParseException {
        JSExpr expr = readConditionnalInclusiveOrExpr();

        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        if (c == '?') {
            buffer.skip();
            JSExpr onTrueExpr = readExpr();
            buffer.skipWhiteSpace();
            buffer.readExpected(':', "ternary expression");
            JSExpr onFalseExpr = readExpr();
            expr = new TernaryJSExpr(expr, onTrueExpr, onFalseExpr);
        }

        return expr;
    }

    private JSExpr readConditionnalInclusiveOrExpr() throws ParseException {
        JSExpr expr = readConditionnalAndExpr();

        buffer.skipWhiteSpace();
        Character c1 = buffer.readAhead();
        Character c2 = buffer.readAhead(2);
        if (c1 == '|' && c2 == '|') {
            buffer.skip(2);
            JSExpr rightExpr = readConditionnalAndExpr();
            expr = new BooleanJSExpr(BooleanOp.OR, expr, rightExpr);
        }

        return expr;
    }

    private JSExpr readConditionnalAndExpr() throws ParseException {
        JSExpr expr = readInclusiveOrExpr();

        buffer.skipWhiteSpace();
        Character c1 = buffer.readAhead();
        Character c2 = buffer.readAhead(2);
        if (c1 == '&' && c2 == '&') {
            buffer.skip(2);
            JSExpr rightExpr = readInclusiveOrExpr();
            expr = new BooleanJSExpr(BooleanOp.AND, expr, rightExpr);
        }

        return expr;
    }

    private JSExpr readInclusiveOrExpr() throws ParseException {
        JSExpr expr = readExclusiveOrExpr();

        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        if (c == '|') {
            buffer.skip();
            JSExpr rightExpr = readExclusiveOrExpr();
            expr = new BitwiseJSExpr(BitwiseOp.OR, expr, rightExpr);
        }

        return expr;
    }

    private JSExpr readExclusiveOrExpr() throws ParseException {
        JSExpr expr = readAndExpr();

        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        if (c == '^') {
            buffer.skip();
            JSExpr rightExpr = readAndExpr();
            expr = new BitwiseJSExpr(BitwiseOp.XOR, expr, rightExpr);
        }

        return expr;
    }

    private JSExpr readAndExpr() throws ParseException {
        JSExpr expr = readEqualityExpr();

        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        if (c == '&') {
            buffer.skip();
            JSExpr rightExpr = readEqualityExpr();
            expr = new BitwiseJSExpr(BitwiseOp.AND, expr, rightExpr);
        }

        return expr;
    }

    private JSExpr readEqualityExpr() throws ParseException {
        JSExpr expr = readRelationalExpr();

        buffer.skipWhiteSpace();
        Character c1 = buffer.readAhead();
        Character c2 = buffer.readAhead(2);
        if (c1 == '=' && c2 == '=') {
            buffer.skip(2);
            JSExpr rightExpr = readRelationalExpr();
            expr = new CompareJSExpr(CompareOp.EQ, expr, rightExpr);
        } else if (c1 == '!' && c2 == '=') {
            buffer.skip(2);
            JSExpr rightExpr = readRelationalExpr();
            expr = new CompareJSExpr(CompareOp.NE, expr, rightExpr);
        }

        return expr;
    }

    private JSExpr readRelationalExpr() throws ParseException {
        JSExpr expr = readShiftExpr();

        buffer.skipWhiteSpace();
        Character c1 = buffer.readAhead();
        Character c2 = buffer.readAhead(2);
        if (c1 == '<' && c2 == '=') {
            buffer.skip(2);
            JSExpr rightExpr = readShiftExpr();
            expr = new CompareJSExpr(CompareOp.LE, expr, rightExpr);
        } else if (c1 == '<') {
            buffer.skip();
            JSExpr rightExpr = readShiftExpr();
            expr = new CompareJSExpr(CompareOp.LT, expr, rightExpr);
        } else if (c1 == '>' && c2 == '=') {
            buffer.skip(2);
            JSExpr rightExpr = readShiftExpr();
            expr = new CompareJSExpr(CompareOp.GE, expr, rightExpr);
        } else if (c1 == '>') {
            buffer.skip();
            JSExpr rightExpr = readShiftExpr();
            expr = new CompareJSExpr(CompareOp.GT, expr, rightExpr);
        }

        return expr;
    }

    private JSExpr readShiftExpr() throws ParseException {
        JSExpr expr = readAdditiveExpr();

        buffer.skipWhiteSpace();
        Character c1 = buffer.readAhead();
        Character c2 = buffer.readAhead(2);
        Character c3 = buffer.readAhead(3);
        if (c1 == '<' && c2 == '<') {
            buffer.skip(2);
            JSExpr rightExpr = readAdditiveExpr();
            expr = new ShiftJSExpr(ShiftOp.LEFT, expr, rightExpr);
        } else if (c1 == '>' && c2 == '>' && c3 == '>') {
            buffer.skip(3);
            JSExpr rightExpr = readAdditiveExpr();
            expr = new ShiftJSExpr(ShiftOp.LOGICAL_RIGHT, expr, rightExpr);
        } else if (c1 == '>' && c2 == '>') {
            buffer.skip(2);
            JSExpr rightExpr = readAdditiveExpr();
            expr = new ShiftJSExpr(ShiftOp.RIGHT, expr, rightExpr);
        }

        return expr;
    }

    private JSExpr readAdditiveExpr() throws ParseException {
        JSExpr expr = readMultiplicativeExpr();

        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        if (c == '+') {
            buffer.skip();
            JSExpr rightExpr = readMultiplicativeExpr();
            expr = new ArithmeticJSExpr(ArithmeticOp.PLUS, expr, rightExpr);
        } else if (c == '-') {
            buffer.skip();
            JSExpr rightExpr = readMultiplicativeExpr();
            expr = new ArithmeticJSExpr(ArithmeticOp.MINUS, expr, rightExpr);
        }

        return expr;
    }

    private JSExpr readMultiplicativeExpr() throws ParseException {
        JSExpr expr = readUnaryExpr();

        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        if (c == '*') {
            buffer.skip();
            JSExpr rightExpr = readUnaryExpr();
            expr = new ArithmeticJSExpr(ArithmeticOp.MULT, expr, rightExpr);
        } else if (c == '/') {
            buffer.skip();
            JSExpr rightExpr = readUnaryExpr();
            expr = new ArithmeticJSExpr(ArithmeticOp.DIV, expr, rightExpr);
        } else if (c == '%') {
            buffer.skip();
            JSExpr rightExpr = readUnaryExpr();
            expr = new ArithmeticJSExpr(ArithmeticOp.MODULO, expr, rightExpr);
        }

        return expr;
    }

    private JSExpr readUnaryExpr() throws ParseException {
        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        JSExpr expr;
        if (c == '+') {
            buffer.skip();
            expr = new UnaryJSExpr(UnaryOp.PLUS, readUnaryExpr());
        } else if (c == '-') {
            buffer.skip();
            expr = new UnaryJSExpr(UnaryOp.MINUS, readUnaryExpr());
        } else {
            expr = readUnaryExprNotPlusMinus();
        }
        return expr;
    }

    private JSExpr readUnaryExprNotPlusMinus() throws ParseException {
        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        JSExpr expr;
        if (c == '~') {
            buffer.skip();
            expr = new UnaryJSExpr(UnaryOp.NOT_BITWISE, readUnaryExpr());
        } else if (c == '!') {
            buffer.skip();
            expr = new UnaryJSExpr(UnaryOp.NOT, readUnaryExpr());
        } else {
            expr = readPrimaryExpr();
            expr = readSelectorExpr(expr);
        }
        return expr;
    }

    private JSExpr readPrimaryExpr() throws ParseException {
        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        JSExpr expr;
        if (c == '(') {
            buffer.skip();
            expr = readExpr();
            buffer.skipWhiteSpace();
            buffer.readExpected(')', "parenthesed expression");
        } else if (c == '@') {
            buffer.skip();
            expr = new ThisJSExpr();
            buffer.skipWhiteSpace();
            Character c2 = buffer.readAhead();
            if (c2 == '.') {
                String lastId = null;
                do {
                    buffer.skip();
                    if (lastId != null) {
                        expr = new FieldSelectorJSExpr(expr, lastId);
                    }
                    lastId = readIdentifier();
                    buffer.skipWhiteSpace();
                } while (buffer.readAhead() == '.');
                expr = readIdentifierSuffix(expr, lastId);
            }
        } else {
            expr = readLiteral();
        }

        return expr;
    }

    private JSExpr readLiteral() throws ParseException {
        Character c = buffer.readAhead();
        if (c == null) {
            return null;
        }
        if (c == '\'') {
            buffer.skip();
            String value = readEscaped('\'');
            buffer.readExpected('\'', "end of string \'");
            return new LiteralExpr(value);
        }
        if (c == '"') {
            buffer.skip();
            String value = readEscaped('"');
            buffer.readExpected('"', "end of string \"");
            return new LiteralExpr(value);
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
            return new LiteralExpr(Long.parseLong(numberBuffer.toString()));
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
            return new LiteralExpr(Long.parseLong(numberBuffer.toString()));
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
            return new LiteralExpr(Long.parseLong(numberBuffer.toString()));
        } else if (c >= '0' && c <= '9') {
            // number
            StringBuilder numberBuffer = new StringBuilder();
            while (c != null) {
                if (c >= '0' && c <= '9' || c == '_') {
                    numberBuffer.append(c);
                } else {
                    break;
                }
                buffer.skip();
                c = buffer.readAhead();
            }
            if (c == '.') {
                appendFloatingPoint(numberBuffer);
                return new LiteralExpr(Double.parseDouble(numberBuffer.toString()));
            }
            if (c == 'f' || c == 'F' || c == 'd' || c == 'D') {
                numberBuffer.append(c);
                buffer.skip();
                return new LiteralExpr(Double.parseDouble(numberBuffer.toString()));
            }
            if (c == 'l' || c == 'L') {
                numberBuffer.append(c);
                buffer.skip();
            }
            return new LiteralExpr(Long.parseLong(numberBuffer.toString()));
        } else if (c == '.' && c2 != null && c2 >= '0' && c2 <= '9') {
            // double number
            StringBuilder numberBuffer = new StringBuilder();
            appendFloatingPoint(numberBuffer);
            return new LiteralExpr(Double.parseDouble(numberBuffer.toString()));
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
        if (c == 'f' || c == 'F' || c == 'd' || c == 'D') {
            numberBuffer.append(c);
            buffer.skip();
        }
    }

    private JSExpr readSelectorExpr(JSExpr expr) throws ParseException {
        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        if (c == '.') {
            buffer.skip();
            String id = readIdentifier();
            List<JSExpr> arguments = readArguments();
            if (arguments != null) {
                expr = new MethodCallJSExpr(expr, id, arguments);
            } else {
                expr = new FieldSelectorJSExpr(expr, id);
            }
        } else if (c == '[') {
            buffer.skip();
            JSExpr indexExpr = readExpr();
            expr = new ArraySelectorJSExpr(expr, indexExpr);
            buffer.skipWhiteSpace();
            buffer.readExpected(']', "array selector");
        } else {
            return expr;
        }
        readSelectorExpr(expr);
        return expr;
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
        return name.toString();
    }

    private boolean isAlpha(Character c) {
        return c != null && (c == '_' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z');
    }

    private boolean isNum(Character c) {
        return c != null && (c >= '0' && c <= '9');
    }

    private List<JSExpr> readArguments() throws ParseException {
        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        List<JSExpr> arguments;
        if (c == '(') {
            arguments = new ArrayList<JSExpr>();
            do {
                buffer.skip();
                JSExpr argument = readExpr();
                arguments.add(argument);
                buffer.skipWhiteSpace();
            } while (buffer.readAhead() == ',');
        } else {
            arguments = null;
        }
        return arguments;
    }

    private JSExpr readIdentifierSuffix(JSExpr expr, String id) throws ParseException {
        buffer.skipWhiteSpace();
        Character c = buffer.readAhead();
        if (c == '[') {
            expr = new FieldSelectorJSExpr(expr, id);
            do {
                buffer.skip();
                JSExpr index = readExpr();
                expr = new ArraySelectorJSExpr(expr, index);
                buffer.readExpected(']', "array selector");
                buffer.skipWhiteSpace();
            } while (buffer.readAhead() == '[');
        } else if (c == '(') {
            List<JSExpr> arguments = readArguments();
            if (arguments != null) {
                expr = new MethodCallJSExpr(expr, id, arguments);
            }
        } else {
            expr = new FieldSelectorJSExpr(expr, id);            
        }
        return expr;
    }
}
