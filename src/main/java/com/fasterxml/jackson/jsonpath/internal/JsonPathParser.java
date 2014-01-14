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

import com.fasterxml.jackson.jsonpath.internal.js.JSExpr;
import com.fasterxml.jackson.jsonpath.internal.js.JSExprParser;

public class JsonPathParser {

    private static final char ROOT = '$';
    private static final char DOT = '.';
    private static final char SELECT_OPEN = '[';
    private static final char SELECT_CLOSE = ']';
    private static final char EXPR_OPEN = '(';
    private static final char EXPR_CLOSE = ')';
    private static final char CONDITION = '?';
    private static final char WILDCARD = '*';
    private static final char QUOTE = '"';
    private static final char APOS = '\'';

    private Buffer buffer;

    private List<JsonPathEvaluator> evaluators = new ArrayList<JsonPathEvaluator>();

    JsonPathParser(String path) {
        buffer = new Buffer(path);
    }

    public static List<JsonPathEvaluator> parse(String path) throws ParseException {
        JsonPathParser parser = new JsonPathParser(path);
        parser.parse();
        return parser.evaluators;
    }

    private void parse() throws ParseException {
        Character c = buffer.read();
        if (c == null) {
            throw new ParseException("Empty path", buffer.pos);
        }
        if (c != ROOT) {
            throw new ParseException("Expected to start with root '" + ROOT + "'", buffer.pos);
        }

        while ((c = buffer.readAhead()) != null) {
            switch (c) {
            case SELECT_OPEN:
                buffer.skip();
                readSelector();
                buffer.readExpected(SELECT_CLOSE, "an array selector");
                break;
            case '.':
                buffer.skip();
                c = buffer.readAheadNotEnd("a selector");
                if (c == WILDCARD) {
                    buffer.skip();
                    evaluators.add(new JsonWildcardEvaluator());
                } else if (c == DOT) {
                    buffer.skip();
                    evaluators.add(new JsonFieldDescendingEvaluator());
                    if (buffer.readAhead() == DOT) {
                        throw new ParseException("Unexpected sequence of 3 dots", buffer.pos);
                    }
                } else {
                    String name = readName();
                    evaluators.add(new JsonFieldEvaluator(name));
                }
                break;
            default:
                readName();
                break;
            }
        }
    }

    private void readSelector() throws ParseException {
        Character c = buffer.readAheadNotEnd("a selector");
        switch (c) {
        case APOS:
            buffer.skip();
            String name = readEscaped(APOS);
            evaluators.add(new JsonFieldEvaluator(name));
            buffer.readExpected(APOS, "a selector");
            break;
        case QUOTE:
            buffer.skip();
            name = readEscaped('\"');
            evaluators.add(new JsonFieldEvaluator(name));
            buffer.readExpected(QUOTE, "a selector");
            break;
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9': {
            int n = readNum();
            evaluators.add(new JsonIndexArrayEvaluator(n));
            break;
        }
        case CONDITION: {
            JSExpr expr = readCondition();
            evaluators.add(new JsonScriptFilterEvaluator(expr));
            break;
        }
        case EXPR_OPEN: {
            JSExpr expr = readExpression();
            evaluators.add(new JsonScriptArrayEvaluator(expr));
            break;
        }
        case WILDCARD: {
            buffer.skip();
            evaluators.add(new JsonWildcardSelectorEvaluator());
            break;            
        }
        default:
            throw new ParseException("Unexpected character '" + c + "' in selector ", buffer.pos);
        }
    }

    private int readNum() {
        int n = 0;
        Character c = null;
        while ((c = buffer.readAhead()) != null) {
            if (c < '0' || c > '9') {
                break;
            }
            buffer.skip();
            n = 10 * n + (c - '0');
        }
        return n;
    }

    private String readEscaped(char endChar) throws ParseException {
        StringBuilder value = new StringBuilder();
        Character c = null;
        while ((c = buffer.readAhead()) != null) {
            if (c == endChar) {
                break;
            }
            buffer.skip();
            if (c == '\\') {
                c = buffer.readNotEnd("an escaped character");
            }
            value.append(c);
        }
        return value.toString();
    }

    private JSExpr readCondition() throws ParseException {
        buffer.readExpected(CONDITION, "a condition");
        JSExpr expr = readExpression();
        return expr;
    }

    private JSExpr readExpression() throws ParseException {
        buffer.readExpected(EXPR_OPEN, "a js expression");
        JSExprParser jsParser = new JSExprParser(buffer);
        jsParser.parse();
        JSExpr expr = jsParser.getExpression();
        buffer.readExpected(EXPR_CLOSE, "a js expression");
        return expr;
    }

    private String readName() {
        StringBuilder name = new StringBuilder();
        Character c = null;
        while ((c = buffer.readAhead()) != null) {
            if (!(c == '_' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || name.length() > 0 && c >= '0' && c <= '9')) {
                break;
            }
            buffer.skip();
            name.append(c);
        }
        return name.toString();
    }

}
