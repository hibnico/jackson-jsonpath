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

import static com.fasterxml.jackson.jsonpath.jsonassert.JsonAssert.with;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

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

    @Test
    public void array_start_expands() throws Exception {
        with(ARRAY_EXPAND).assertThat("$[?(@['parent'] == 'ONE')].child.name", hasItems("NAME_ONE"));
    }

    @Test
    public void bracket_notation_can_be_used_in_path() throws Exception {
        assertEquals("new", JsonPath.eval(DOCUMENT, "$.['store'].bicycle.['dot.notation']").toNode().asText());
        assertEquals("new", JsonPath.eval(DOCUMENT, "$['store']['bicycle']['dot.notation']").toNode().asText());
        assertEquals("new", JsonPath.eval(DOCUMENT, "$.['store']['bicycle']['dot.notation']").toNode().asText());
        assertEquals("new", JsonPath.eval(DOCUMENT, "$.['store'].['bicycle'].['dot.notation']").toNode().asText());

        assertEquals("dashes", JsonPath.eval(DOCUMENT, "$.['store'].bicycle.['dash-notation']").toNode().asText());
        assertEquals("dashes", JsonPath.eval(DOCUMENT, "$['store']['bicycle']['dash-notation']").toNode().asText());
        assertEquals("dashes", JsonPath.eval(DOCUMENT, "$.['store']['bicycle']['dash-notation']").toNode().asText());
        assertEquals("dashes", JsonPath.eval(DOCUMENT, "$.['store'].['bicycle'].['dash-notation']").toNode().asText());
    }

    @Test
    public void filter_an_array() throws Exception {
        JsonNode node = JsonPath.eval(ARRAY, "$.[?(@.value == 1)]").toNode();
        assertEquals(1, node.size());
    }

    @Test
    public void filter_an_array_on_index() throws Exception {
        JsonNode matches = JsonPath.eval(ARRAY, "$.[1].value").toNode();
        assertEquals(2, matches.asInt());
    }

    @Test
    public void read_path_with_colon() throws Exception {
        assertEquals(JsonPath.eval(DOCUMENT, "$.store.bicycle.foo:bar").toNode().asText(), "fooBar");
        assertEquals(JsonPath.eval(DOCUMENT, "$['store']['bicycle']['foo:bar']").toNode().asText(), "fooBar");
    }

    @Test
    public void read_document_from_root() throws Exception {
        JsonNode node = JsonPath.eval(DOCUMENT, "$.store").toNode();
        assertEquals(2, node.size());
    }

    @Test
    public void read_store_book_1() throws Exception {
        JsonPath path = JsonPath.compile("$.store.book[1]");
        JsonNode node = path.eval(DOCUMENT).toNode();
        assertEquals("Evelyn Waugh", node.get("author").asText());
    }

    @Test
    public void read_store_book_wildcard() throws Exception {
        with(DOCUMENT).assertThat("$.store.book[*]", not(empty()));
    }

    @Test
    public void read_store_book_author() throws Exception {
        // @formatter:off
        with(DOCUMENT)
            .assertThat("$.store.book[0,1].author", hasItems("Nigel Rees", "Evelyn Waugh"))
            .assertThat("$.store.book[*].author", hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"))
            .assertThat("$.['store'].['book'][*].['author']", hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"))
            .assertThat("$['store']['book'][*]['author']", hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"))
            .assertThat("$['store'].book[*]['author']", hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
        // @formatter:on
    }

    @Test
    public void all_authors() throws Exception {
        with(DOCUMENT).assertThat("$..author", hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
    }

    @Test
    public void all_store_properties() throws Exception {
        JsonNode node = JsonPath.eval(DOCUMENT, "$.store.*").toNode();
        assertEquals(JsonPath.eval(node, "$.[0].[0].author").toNode().asText(), "Nigel Rees");
        assertEquals(JsonPath.eval(node, "$.[0][0].author").toNode().asText(), "Nigel Rees");
    }

    @Test
    public void all_prices_in_store() throws Exception {
        with(DOCUMENT).assertThat("$.store..['display-price']", hasItems(8.95D, 12.99D, 8.99D, 19.95D));
    }

    @Test
    public void access_array_by_index_from_tail() throws Exception {
        // @formatter:off
        with(DOCUMENT)
            .assertThat("$..book[(@.length-1)].author", equalTo("J. R. R. Tolkien"))
            .assertThat("$..book[-1:].author", equalTo("J. R. R. Tolkien"));
        // @formatter:on
    }

    @Test
    public void read_store_book_index_0_and_1() throws Exception {
        with(DOCUMENT).assertThat("$.store.book[0,1].author", hasItems("Nigel Rees", "Evelyn Waugh"));
        assertTrue(JsonPath.eval(DOCUMENT, "$.store.book[0,1].author").toNode().size() == 2);
    }

    @Test
    public void read_store_book_pull_first_2() throws Exception {
        with(DOCUMENT).assertThat("$.store.book[:2].author", hasItems("Nigel Rees", "Evelyn Waugh"));
        assertTrue(JsonPath.eval(DOCUMENT, "$.store.book[:2].author").toNode().size() == 2);
    }

    @Test
    public void read_store_book_filter_by_isbn() throws Exception {
        with(DOCUMENT).assertThat("$.store.book[?(@.isbn)].isbn", hasItems("0-553-21311-3", "0-395-19395-8"));
        assertTrue(JsonPath.eval(DOCUMENT, "$.store.book[?(@.isbn)].isbn").toNode().size() == 2);
        assertTrue(JsonPath.eval(DOCUMENT, "$.store.book[?(@['isbn'])].isbn").toNode().size() == 2);
    }

    @Test
    public void all_books_cheaper_than_10() throws Exception {
        with(DOCUMENT).assertThat("$..book[?(@['display-price'] < 10)].title", hasItems("Sayings of the Century", "Moby Dick"));
        with(DOCUMENT).assertThat("$..book[?(@.display-price < 10)].title", hasItems("Sayings of the Century", "Moby Dick"));
    }

    @Test
    public void all_books() throws Exception {
        with(DOCUMENT).assertThat("$..book", not(empty()));
    }

    @Test
    public void dot_in_predicate_works() throws Exception {
        with(PRODUCT_JSON).assertThat("$.product[?(@.version=='4.0')].codename", hasItems("Montreal"));

    }

    @Test
    public void dots_in_predicate_works() throws Exception {
        with(PRODUCT_JSON).assertThat("$.product[?(@.attr.with.dot=='A')].codename", hasItems("Seattle"));
    }

    @Test
    public void all_books_with_category_reference() throws Exception {
        with(DOCUMENT).assertThat("$..book[?(@.category=='reference')].title", hasItems("Sayings of the Century"));
        with(DOCUMENT).assertThat("$.store.book[?(@.category=='reference')].title", hasItems("Sayings of the Century"));
    }

    @Test
    public void all_members_of_all_documents() throws Exception {
        with(DOCUMENT).assertThat("$..*", not(empty()));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void access_index_out_of_bounds_does_not_throw_exception() throws Exception {
        JsonPath.eval(DOCUMENT, "$.store.book[100].author");
    }

}
