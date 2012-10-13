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
package com.fasterxml.jackson.jsonpath.jsonassert;

import static com.fasterxml.jackson.jsonpath.jsonassert.JsonAssert.collectionWithSize;
import static com.fasterxml.jackson.jsonpath.jsonassert.JsonAssert.emptyCollection;
import static com.fasterxml.jackson.jsonpath.jsonassert.JsonAssert.mapContainingKey;
import static com.fasterxml.jackson.jsonpath.jsonassert.JsonAssert.mapContainingValue;
import static com.fasterxml.jackson.jsonpath.jsonassert.JsonAssert.with;
import static com.fasterxml.jackson.jsonpath.jsonassert.JsonAssert.withResource;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class JsonAssertTest {

    // @formatter:off
    public final static String JSON =
            "{ \"store\": {\n" +
                    "    \"book\": [ \n" +
                    "      { \"category\": \"reference\",\n" +
                    "        \"author\": \"Nigel Rees\",\n" +
                    "        \"title\": \"Sayings of the Century\",\n" +
                    "        \"price\": 8.95\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"Evelyn Waugh\",\n" +
                    "        \"title\": \"Sword of Honour\",\n" +
                    "        \"price\": 12.99\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"Herman Melville\",\n" +
                    "        \"title\": \"Moby Dick\",\n" +
                    "        \"isbn\": \"0-553-21311-3\",\n" +
                    "        \"price\": 8.99\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"J. R. R. Tolkien\",\n" +
                    "        \"title\": \"The Lord of the Rings\",\n" +
                    "        \"isbn\": \"0-395-19395-8\",\n" +
                    "        \"price\": 22.99\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"bicycle\": {\n" +
                    "      \"color\": \"red\",\n" +
                    "      \"price\": 19.95\n," +
                    "      \"nullValue\": null\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
    // @formatter:on

    @Test(expected = AssertionError.class)
    public void failed_error_message() throws Exception {
        with(JSON).assertThat("$.store.book[0].category", endsWith("foobar"));
    }

    @Test
    public void links_document() throws Exception {
        // @formatter:off
        withResource("links.json")
                .assertEquals("count", 2)
                .assertThat("links.gc:this.href", endsWith("?pageNumber=1&pageSize=2"))
                .assertNotDefined("links.gc:prev")
                .assertNotDefined("links.gc:next")
                .assertThat("rows", collectionWithSize(equalTo(2)));
        // @formatter:on

    }

    @Test
    public void a_document_can_be_expected_not_to_contain_a_path() throws Exception {
        with(JSON).assertNotDefined("$.store.bicycle.cool");
    }

    @Test
    public void a_value_can_asserted_to_be_null() throws Exception {
        with(JSON).assertNull("$.store.bicycle.nullValue");
    }

    @Test
    public void ends_with_evalueates() throws Exception {
        with(JSON).assertThat("$.store.book[0].category", endsWith("nce"));
    }

    @Test
    public void a_path_can_be_asserted_with_matcher() throws Exception {

        with(JSON).assertThat("$.store.bicycle.color", equalTo("red")).assertThat("$.store.bicycle.price", equalTo(19.95D));
    }

    @Test
    public void list_content_can_be_asserted_with_matcher() throws Exception {

        with(JSON).assertThat("$..book[*].author", hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));

        with(JSON).assertThat("$..author", hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien")).assertThat("$..author",
                is(collectionWithSize(equalTo(4))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void list_content_can_be_asserted_with_nested_matcher() throws Exception {
        with(JSON).assertThat("$..book[*]", hasItems(hasEntry("author", "Nigel Rees"), hasEntry("author", "Evelyn Waugh")));
    }

    @Test
    public void map_content_can_be_asserted_with_matcher() throws Exception {
        // @formatter:off
        with(JSON).assertThat("$.store.book[0]", hasEntry("category", "reference"))
                .assertThat("$.store.book[0]", hasEntry("title", "Sayings of the Century"))
                .and()
                .assertThat("$..book[0]", hasEntry("category", "reference"))
                .and()
                .assertThat("$.store.book[0]", mapContainingKey(equalTo("category")))
                .and()
                .assertThat("$.store.book[0]", mapContainingValue(equalTo("reference")));

        with(JSON).assertThat("$.['store'].['book'][0]", hasEntry("category", "reference"))
                .assertThat("$.['store'].['book'][0]", hasEntry("title", "Sayings of the Century"))
                .and()
                .assertThat("$..['book'][0]", hasEntry("category", "reference"))
                .and()
                .assertThat("$.['store'].['book'][0]", mapContainingKey(equalTo("category")))
                .and()
                .assertThat("$.['store'].['book'][0]", mapContainingValue(equalTo("reference")));
        // @formatter:on
    }

    @Test
    public void an_empty_collection() throws Exception {
        with(JSON).assertThat("$.store.book[?(@.category = 'x')]", emptyCollection());
    }

    @Test
    public void a_path_can_be_asserted_equal_to() throws Exception {
        // @formatter:off
        with(JSON)
            .assertEquals("$.store.book[0].title", "Sayings of the Century")
            .assertThat("$.store.book[0].title", equalTo("Sayings of the Century"));

        with(JSON)
            .assertEquals("$['store']['book'][0].['title']", "Sayings of the Century")
            .assertThat("$['store'].book[0].title", equalTo("Sayings of the Century"));
        // @formatter:on
    }

    @Test
    public void invalid_path() throws Exception {
        with(JSON).assertThat("$.store.book[*].fooBar", emptyCollection());
    }

    @Test
    public void path_including_wildcard_path_followed_by_another_path_concatenates_results_to_list() throws Exception {
        withResource("lotto.json").assertThat("lotto.winners[*].winnerId", hasItems(23, 54));
    }

}
