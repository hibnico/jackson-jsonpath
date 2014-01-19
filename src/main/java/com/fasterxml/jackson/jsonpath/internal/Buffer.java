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

class Buffer {

    private char[] input;

    int pos = -1;

    Buffer(String input) {
        this.input = input.toCharArray();
    }

    Character readAhead() {
        return readAhead(1);
    }

    Character readAhead(int n) {
        if (pos + n >= input.length) {
            return null;
        }
        return input[pos + n];
    }

    Character read() {
        Character c = readAhead();
        if (c == null) {
            return null;
        }
        pos++;
        return c;
    }

    void skip() {
        skip(1);
    }

    void skip(int n) {
        pos += n;
    }

    void skipWhiteSpace() {
        Character c = null;
        while ((c = readAhead()) != null) {
            if (c != ' ' && c != '\t') {
                return;
            }
            skip();
        }
    }

    char readAheadNotEnd(String name) throws ParseException {
        Character c = readAhead();
        if (c == null) {
            throw new ParseException("Unexpected end of input in " + name, pos);
        }
        return c;
    }

    char readNotEnd(String name) throws ParseException {
        Character c = read();
        if (c == null) {
            throw new ParseException("Unexpected end of input in " + name, pos);
        }
        return c;
    }

    void readExpected(char expected, String name) throws ParseException {
        Character c = read();
        if (c == null) {
            throw new ParseException("Unexpected end of input in " + name, pos);
        }
        if (c != expected) {
            throw new ParseException("Expecting '" + expected + "' but found '" + c + "' in " + name, pos);
        }
    }

    boolean isConsumed() {
        return pos >= input.length - 1;
    }

}
