package com.jayway.jsonassert.impl;

import static java.lang.String.format;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.codehaus.jackson.JsonNode;
import org.hamcrest.Matcher;

import com.jayway.jsonassert.JsonAsserter;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonNodeUtil;
import com.jayway.jsonpath.JsonPath;

/**
 * User: kalle stenflo Date: 1/21/11 Time: 3:43 PM
 */
public class JsonAsserterImpl implements JsonAsserter {

    private final JsonNode jsonObject;

    /**
     * Instantiates a new JSONAsserter
     * 
     * @param jsonObject the object to make asserts on
     */
    public JsonAsserterImpl(JsonNode jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**
     * {@inheritDoc}
     */
    public <T> JsonAsserter assertThat(String path, Matcher<T> matcher) {
        JsonNode node = JsonPath.read(jsonObject, path);
        Object obj = JsonNodeUtil.asJava(node);
        if (!matcher.matches(obj)) {
            throw new AssertionError(String.format("JSON doesn't match.\nExpected:\n%s\nActual:\n%s", matcher.toString(), obj));
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public <T> JsonAsserter assertEquals(String path, T expected) {
        return assertThat(path, equalTo(expected));
    }

    /**
     * {@inheritDoc}
     */
    public JsonAsserter assertNotDefined(String path) {
        try {
            JsonPath.read(jsonObject, path);
            throw new AssertionError(format("Document contains the path <%s> but was expected not to.", path));
        } catch (InvalidPathException e) {
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public JsonAsserter assertNull(String path) {
        return assertThat(path, nullValue());
    }

    /**
     * {@inheritDoc}
     */
    public <T> JsonAsserter assertNotNull(String path) {
        return assertThat(path, notNullValue());
    }

    /**
     * {@inheritDoc}
     */
    public JsonAsserter and() {
        return this;
    }

}
