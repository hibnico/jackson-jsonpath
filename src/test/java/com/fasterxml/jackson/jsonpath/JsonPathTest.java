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
package com.fasterxml.jackson.jsonpath;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.hamcrest.Matcher;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonNodeUtil;
import com.fasterxml.jackson.jsonpath.JsonPath;

public class JsonPathTest {

    public final static String ARRAY = "[{\"value\": 1},{\"value\": 2}, {\"value\": 3},{\"value\": 4}]";

    // @formatter:off
    public final static String DOCUMENT =
            "{ \"store\": {\n" +
                    "    \"book\": [ \n" +
                    "      { \"category\": \"reference\",\n" +
                    "        \"author\": \"Nigel Rees\",\n" +
                    "        \"title\": \"Sayings of the Century\",\n" +
                    "        \"display-price\": 8.95\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"Evelyn Waugh\",\n" +
                    "        \"title\": \"Sword of Honour\",\n" +
                    "        \"display-price\": 12.99\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"Herman Melville\",\n" +
                    "        \"title\": \"Moby Dick\",\n" +
                    "        \"isbn\": \"0-553-21311-3\",\n" +
                    "        \"display-price\": 8.99\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"J. R. R. Tolkien\",\n" +
                    "        \"title\": \"The Lord of the Rings\",\n" +
                    "        \"isbn\": \"0-395-19395-8\",\n" +
                    "        \"display-price\": 22.99\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"bicycle\": {\n" +
                    "      \"color\": \"red\",\n" +
                    "      \"display-price\": 19.95,\n" +
                    "      \"foo:bar\": \"fooBar\",\n" +
                    "      \"dot.notation\": \"new\",\n" +
	                "      \"dash-notation\": \"dashes\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";


    private final static String PRODUCT_JSON = "{\n" +
            "\t\"product\": [ {\n" +
            "\t    \"version\": \"A\", \n" +
            "\t    \"codename\": \"Seattle\", \n" +
            "\t    \"attr.with.dot\": \"A\"\n" +
            "\t},\n" +
            "\t{\n" +
            "\t    \"version\": \"4.0\", \n" +
            "\t    \"codename\": \"Montreal\", \n" +
            "\t    \"attr.with.dot\": \"B\"\n" +
            "\t}]\n" +
            "}";

    private final static String ARRAY_EXPAND = "[{\"parent\": \"ONE\", \"child\": {\"name\": \"NAME_ONE\"}}, [{\"parent\": \"TWO\", \"child\": {\"name\": \"NAME_TWO\"}}]]";
    // @formatter:on

    @SuppressWarnings("unchecked")
    private <T> void checkList(String json, String jsonPath, Matcher<Iterable<T>> matcher) {
        assertThat((List<T>) JsonNodeUtil.asList(JsonPath.read(json, jsonPath)), matcher);
    }

    @Test
    public void array_start_expands() throws Exception {
        // assertThat(JsonPath.<List<String>>read(ARRAY_EXPAND, "$[?(@.parent = 'ONE')].child.name"), hasItems("NAME_ONE"));
        checkList(ARRAY_EXPAND, "$[?(@['parent'] == 'ONE')].child.name", hasItems("NAME_ONE"));
    }

    @Test
    public void bracket_notation_can_be_used_in_path() throws Exception {
        assertEquals("new", JsonPath.read(DOCUMENT, "$.['store'].bicycle.['dot.notation']").asText());
        assertEquals("new", JsonPath.read(DOCUMENT, "$['store']['bicycle']['dot.notation']").asText());
        assertEquals("new", JsonPath.read(DOCUMENT, "$.['store']['bicycle']['dot.notation']").asText());
        assertEquals("new", JsonPath.read(DOCUMENT, "$.['store'].['bicycle'].['dot.notation']").asText());

        assertEquals("dashes", JsonPath.read(DOCUMENT, "$.['store'].bicycle.['dash-notation']").asText());
        assertEquals("dashes", JsonPath.read(DOCUMENT, "$['store']['bicycle']['dash-notation']").asText());
        assertEquals("dashes", JsonPath.read(DOCUMENT, "$.['store']['bicycle']['dash-notation']").asText());
        assertEquals("dashes", JsonPath.read(DOCUMENT, "$.['store'].['bicycle'].['dash-notation']").asText());
    }

    @Test
    public void filter_an_array() throws Exception {
        JsonNode node = JsonPath.read(ARRAY, "$.[?(@.value == 1)]");
        assertEquals(1, node.size());
    }

    @Test
    public void filter_an_array_on_index() throws Exception {
        JsonNode matches = JsonPath.read(ARRAY, "$.[1].value");
        assertEquals(2, matches.asInt());
    }

    @Test
    public void read_path_with_colon() throws Exception {
        assertEquals(JsonPath.read(DOCUMENT, "$.store.bicycle.foo:bar").asText(), "fooBar");
        assertEquals(JsonPath.read(DOCUMENT, "$['store']['bicycle']['foo:bar']").asText(), "fooBar");
    }

    @Test
    public void read_document_from_root() throws Exception {
        JsonNode node = JsonPath.read(DOCUMENT, "$.store");
        assertEquals(2, node.size());
    }

    @Test
    public void read_store_book_1() throws Exception {
        JsonPath path = JsonPath.compile("$.store.book[1]");
        JsonNode node = path.read(DOCUMENT);
        assertEquals("Evelyn Waugh", node.get("author").asText());
    }

    @Test
    public void read_store_book_wildcard() throws Exception {
        JsonPath path = JsonPath.compile("$.store.book[*]");
        JsonNode node = path.read(DOCUMENT);
    }

    @Test
    public void read_store_book_author() throws Exception {
        checkList(DOCUMENT, "$.store.book[0,1].author", hasItems("Nigel Rees", "Evelyn Waugh"));
        checkList(DOCUMENT, "$.store.book[*].author", hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
        checkList(DOCUMENT, "$.['store'].['book'][*].['author']", hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
        checkList(DOCUMENT, "$['store']['book'][*]['author']", hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
        checkList(DOCUMENT, "$['store'].book[*]['author']", hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
    }

    @Test
    public void all_authors() throws Exception {
        checkList(DOCUMENT, "$..author", hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
    }

    @Test
    public void all_store_properties() throws Exception {
        JsonNode node = JsonPath.read(DOCUMENT, "$.store.*");
        assertEquals(JsonPath.read(node, "$.[0].[0].author").asText(), "Nigel Rees");
        assertEquals(JsonPath.read(node, "$.[0][0].author").asText(), "Nigel Rees");
    }

    @Test
    public void all_prices_in_store() throws Exception {
        checkList(DOCUMENT, "$.store..['display-price']", hasItems(8.95D, 12.99D, 8.99D, 19.95D));
    }

    @Test
    public void access_array_by_index_from_tail() throws Exception {
        assertThat(JsonPath.read(DOCUMENT, "$..book[(@.length-1)].author").asText(), equalTo("J. R. R. Tolkien"));
        assertThat(JsonPath.read(DOCUMENT, "$..book[-1:].author").asText(), equalTo("J. R. R. Tolkien"));
    }

    @Test
    public void read_store_book_index_0_and_1() throws Exception {
        checkList(DOCUMENT, "$.store.book[0,1].author", hasItems("Nigel Rees", "Evelyn Waugh"));
        assertTrue(JsonPath.read(DOCUMENT, "$.store.book[0,1].author").size() == 2);
    }

    @Test
    public void read_store_book_pull_first_2() throws Exception {
        checkList(DOCUMENT, "$.store.book[:2].author", hasItems("Nigel Rees", "Evelyn Waugh"));
        assertTrue(JsonPath.read(DOCUMENT, "$.store.book[:2].author").size() == 2);
    }

    @Test
    public void read_store_book_filter_by_isbn() throws Exception {
        checkList(DOCUMENT, "$.store.book[?(@.isbn)].isbn", hasItems("0-553-21311-3", "0-395-19395-8"));
        assertTrue(JsonPath.read(DOCUMENT, "$.store.book[?(@.isbn)].isbn").size() == 2);
        assertTrue(JsonPath.read(DOCUMENT, "$.store.book[?(@['isbn'])].isbn").size() == 2);
    }

    @Test
    public void all_books_cheaper_than_10() throws Exception {
        checkList(DOCUMENT, "$..book[?(@['display-price'] < 10)].title", hasItems("Sayings of the Century", "Moby Dick"));
        checkList(DOCUMENT, "$..book[?(@.display-price < 10)].title", hasItems("Sayings of the Century", "Moby Dick"));
    }

    @Test
    public void all_books() throws Exception {
        JsonNode books = JsonPath.read(DOCUMENT, "$..book");
    }

    @Test
    public void dot_in_predicate_works() throws Exception {
        checkList(PRODUCT_JSON, "$.product[?(@.version=='4.0')].codename", hasItems("Montreal"));

    }

    @Test
    public void dots_in_predicate_works() throws Exception {
        checkList(PRODUCT_JSON, "$.product[?(@.attr.with.dot=='A')].codename", hasItems("Seattle"));
    }

    @Test
    public void all_books_with_category_reference() throws Exception {
        checkList(DOCUMENT, "$..book[?(@.category=='reference')].title", hasItems("Sayings of the Century"));
        checkList(DOCUMENT, "$.store.book[?(@.category=='reference')].title", hasItems("Sayings of the Century"));

    }

    @Test
    public void all_members_of_all_documents() throws Exception {
        JsonNode node = JsonPath.read(DOCUMENT, "$..*");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void access_index_out_of_bounds_does_not_throw_exception() throws Exception {
        JsonNode node = JsonPath.read(DOCUMENT, "$.store.book[100].author");
    }

}
