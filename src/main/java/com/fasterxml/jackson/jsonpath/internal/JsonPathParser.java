package com.fasterxml.jackson.jsonpath.internal;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.jsonpath.internal.js.JavascriptCompiler;
import com.fasterxml.jackson.jsonpath.internal.js.JavascriptExpression;

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

    private JavascriptCompiler jsCompiler;

    private char[] input;

    private int pos = -1;

    private List<JsonPathEvaluator> evaluators = new ArrayList<JsonPathEvaluator>();

    public JsonPathParser(JavascriptCompiler jsCompiler, String path) {
        this.jsCompiler = jsCompiler;
        input = path.toCharArray();
    }

    public List<JsonPathEvaluator> getEvaluators() {
        return evaluators;
    }

    private Character readAhead() {
        return readAhead(1);
    }

    private Character readAhead(int n) {
        if (pos + n >= input.length) {
            return null;
        }
        return input[pos + n];
    }

    private Character read() {
        Character c = readAhead();
        if (c == null) {
            return null;
        }
        pos++;
        return c;
    }

    private void skip() {
        skip(1);
    }

    private void skip(int n) {
        pos += n;
    }

    public void parse() throws ParseException {
        Character c = read();
        if (c == null) {
            throw new ParseException("Empty path", pos);
        }
        if (c != ROOT) {
            throw new ParseException("Expected to start with root '" + ROOT + "'", pos);
        }

        while ((c = readAhead()) != null) {
            switch (c) {
            case SELECT_OPEN:
                skip();
                readSelector();
                readExpected(SELECT_CLOSE, "an array selector");
                break;
            case '.':
                skip();
                c = readAhead();
                if (c == null) {
                    throw new ParseException("Expecting selector after dot", pos);
                } else if (c == WILDCARD) {
                    skip();
                    evaluators.add(new JsonWildcardEvaluator());
                } else if (c == DOT) {
                    skip();
                    evaluators.add(new JsonFieldDescendingEvaluator());
                    if (readAhead() == DOT) {
                        throw new ParseException("Unexpected sequence of 3 dots", pos);
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
        Character c = readAhead();
        checkNotEnd(c, "a selector");
        switch (c) {
        case APOS:
            skip();
            String name = readEscaped(APOS);
            evaluators.add(new JsonFieldEvaluator(name));
            readExpected(APOS, "a selector");
            break;
        case QUOTE:
            skip();
            name = readEscaped('\"');
            evaluators.add(new JsonFieldEvaluator(name));
            readExpected(QUOTE, "a selector");
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
            JavascriptExpression expr = readCondition();
            evaluators.add(new JsonScriptFilterEvaluator(expr));
            break;
        }
        case EXPR_OPEN: {
            JavascriptExpression expr = readExpression();
            evaluators.add(new JsonScriptArrayEvaluator(expr));
            break;
        }
        default:
            throw new ParseException("Unexpected caracter '" + c + "' in selector ", pos);
        }
    }

    private int readNum() {
        int n = 0;
        Character c = null;
        while ((c = readAhead()) != null) {
            if (c < '0' || c > '9') {
                break;
            }
            n = 10 * n + ('9' - c);
        }
        return n;
    }

    private String readEscaped(char endChar) throws ParseException {
        StringBuilder value = new StringBuilder();
        Character c = null;
        while ((c = readAhead()) != null) {
            if (c == endChar) {
                break;
            }
            skip();
            if (c == '\\') {
                c = read();
                checkNotEnd(c, "an escaped caracter");
            }
            value.append(c);
        }
        return value.toString();
    }

    private void checkNotEnd(Character c, String name) throws ParseException {
        if (c == null) {
            throw new ParseException("Unexpected end of input in " + name, pos);
        }
    }

    private JavascriptExpression readCondition() throws ParseException {
        readExpected(CONDITION, "a condition");
        readExpected(EXPR_OPEN, "a condition");
        JavascriptExpression expr = readExpression();
        readExpected(EXPR_CLOSE, "a condition");
        return expr;
    }

    private void readExpected(char expected, String name) throws ParseException {
        Character c = read();
        if (c == null) {
            throw new ParseException("Unexpected end of output in " + name, pos);
        }
        if (c != expected) {
            throw new ParseException("Expecting '" + expected + "' but found '" + c + "' in " + name, pos);
        }
    }

    private JavascriptExpression readExpression() throws ParseException {
        return jsCompiler.compile(doReadExpression());
    }

    private String doReadExpression() throws ParseException {
        StringBuilder script = new StringBuilder();
        Character c = null;
        while ((c = readAhead()) != null) {
            switch (c) {
            case EXPR_CLOSE:
                return script.toString();
            default:
                skip();
                script.append(c);
                break;
            }
        }
        return script.toString();
    }

    private String readName() {
        StringBuilder name = new StringBuilder();
        Character c = null;
        while ((c = readAhead()) != null) {
            if (!(c == '_' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || name.length() > 0 && c >= '0' && c <= '9')) {
                break;
            }
            skip();
            name.append(c);
        }
        return name.toString();
    }

}
