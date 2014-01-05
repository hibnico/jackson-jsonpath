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

import com.fasterxml.jackson.jsonpath.internal.Buffer;

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
        expression = readExpression();
    }

    private JSExpr readExpression() throws ParseException {
        buffer.skipWhiteSpace();
        char c = buffer.readAheadNotEnd("expression");
        if (c == '!') {
            buffer.skip();
            JSExpr expr = readExpression();
            return new NotJSExpr(expr);
        } else if (c == '(') {
            JSExpr expr = readExpression();
            buffer.readExpected(')', "");
            return expr;
        } else {
            throw new ParseException("Expecting an expression", buffer.pos);
        }
    }

}
