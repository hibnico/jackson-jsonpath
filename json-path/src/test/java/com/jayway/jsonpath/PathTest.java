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
package com.jayway.jsonpath;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.codehaus.jackson.JsonNode;
import org.hamcrest.Matcher;
import org.junit.Test;

import com.jayway.jsonpath.internal.PathTokenizer;

public class PathTest {

    Filter filter = new Filter() {
        @Override
        public boolean accept(JsonNode obj) {
            return true;
        }

        @Override
        public Filter addCriteria(Criteria criteria) {
            return this;
        }
    };

    @Test
    public void path_is_not_definite() throws Exception {
        assertFalse(JsonPath.compile("$..book[0]").isPathDefinite());
        assertFalse(JsonPath.compile("$book[?]", filter).isPathDefinite());
        assertFalse(JsonPath.compile("$.books[*]").isPathDefinite());
    }

    @Test
    public void path_is_definite() throws Exception {
        assertTrue(JsonPath.compile("$.definite.this.is").isPathDefinite());
        assertTrue(JsonPath.compile("rows[0].id").isPathDefinite());
    }

    @Test
    public void valid_path_is_split_correctly() throws Exception {
        assertPath("$.store[*]", hasItems("$", "store", "[*]"));
        assertPath("$", hasItems("$"));
        assertPath("$..*", hasItems("$", "..", "*"));
        assertPath("$.store", hasItems("$", "store"));
        assertPath("$.store.*", hasItems("$", "store", "*"));
        assertPath("$.store[*].name", hasItems("$", "store", "[*]", "name"));
        assertPath("$..book[-1:].foo.bar", hasItems("$", "..", "book", "[-1:]", "foo", "bar"));
        assertPath("$..book[?(@.isbn)]", hasItems("$", "..", "book", "[?(@.isbn)]"));
        assertPath("['store'].['price']", hasItems("$", "store", "price"));
        assertPath("$.['store'].['price']", hasItems("$", "store", "price"));
        assertPath("$.['store']['price']", hasItems("$", "store", "price"));
        assertPath("$.['store'].price", hasItems("$", "store", "price"));
        assertPath("$.['store space']['price space']", hasItems("$", "store space", "price space"));
        assertPath("$.['store']['nice.price']", hasItems("$", "store", "nice.price"));
        assertPath("$..book[?(@.price<10)]", hasItems("$", "..", "book", "[?(@.price<10)]"));
        assertPath("$..book[?(@.price<10)]", hasItems("$", "..", "book", "[?(@.price<10)]"));
        assertPath("$.store.book[*].author", hasItems("$", "store", "book", "[*]", "author"));
        assertPath("$.store..price", hasItems("$", "store", "..", "price"));
    }

    @Test
    public void white_space_are_removed() throws Exception {
        assertPath("$.[ 'store' ]", hasItems("$", "store"));
        assertPath("$.[   'store' ]", hasItems("$", "store"));
        assertPath("$.['store bore']", hasItems("$", "store bore"));
        assertPath("$..book[  ?(@.price<10)  ]", hasItems("$", "..", "book", "[?(@.price<10)]"));
        assertPath("$..book[?(@.price<10  )]", hasItems("$", "..", "book", "[?(@.price<10)]"));
        assertPath("$..book[?(  @.price<10)]", hasItems("$", "..", "book", "[?(@.price<10)]"));
        assertPath("$..book[  ?(@.price<10)]", hasItems("$", "..", "book", "[?(@.price<10)]"));
    }

    @Test
    public void dot_ending_ignored() throws Exception {
        assertPath("$..book['something'].", hasItems("$", "..", "something"));
    }

    @Test
    public void invalid_path_throws_exception() throws Exception {
        assertPathInvalid("$...*");
    }

    // ----------------------------------------------------------------
    //
    // Helpers
    //
    // ----------------------------------------------------------------

    private void assertPathInvalid(String path) {
        try {
            new PathTokenizer(path);
            assertTrue("Expected exception!", false);
        } catch (InvalidPathException expected) {
        }
    }

    private void assertPath(String path, Matcher<Iterable<String>> matcher) {
        PathTokenizer tokenizer = new PathTokenizer(path);
        assertThat(tokenizer.getFragments(), matcher);
    }

}
