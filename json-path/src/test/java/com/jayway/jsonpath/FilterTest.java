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

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.regex.Pattern;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;

public class FilterTest {

    // @formatter:off
    public final static String DOCUMENT =
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
                    "      \"price\": 19.95,\n" +
                    "      \"foo:bar\": \"fooBar\",\n" +
                    "      \"dot.notation\": \"new\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
    // @formatter:on

    // -------------------------------------------------
    //
    // Single filter tests
    //
    // -------------------------------------------------
    @Test
    public void is_filters_evaluates() throws Exception {
        ObjectNode check = JsonNodeFactory.instance.objectNode();
        check.put("foo", "foo");
        check.putNull("bar");

        assertTrue(filter(where("bar").is(null)).accept(check));
        assertTrue(filter(where("foo").is("foo")).accept(check));
        assertFalse(filter(where("foo").is("xxx")).accept(check));
        assertFalse(filter(where("bar").is("xxx")).accept(check));
    }

    @Test
    public void ne_filters_evaluates() throws Exception {
        ObjectNode check = JsonNodeFactory.instance.objectNode();
        check.put("foo", "foo");
        check.putNull("bar");

        assertTrue(filter(where("foo").ne(null)).accept(check));
        assertTrue(filter(where("foo").ne("not foo")).accept(check));
        assertFalse(filter(where("foo").ne("foo")).accept(check));
        assertFalse(filter(where("bar").ne(null)).accept(check));
    }

    @Test
    public void gt_filters_evaluates() throws Exception {
        ObjectNode check = JsonNodeFactory.instance.objectNode();
        check.put("foo", 12.5D);
        check.putNull("foo_null");

        assertTrue(filter(where("foo").gt(12D)).accept(check));
        assertFalse(filter(where("foo").gt(null)).accept(check));
        assertFalse(filter(where("foo").gt(20D)).accept(check));
        assertFalse(filter(where("foo_null").gt(20D)).accept(check));
    }

    @Test
    public void gte_filters_evaluates() throws Exception {
        ObjectNode check = JsonNodeFactory.instance.objectNode();
        check.put("foo", 12.5D);
        check.putNull("foo_null");

        assertTrue(filter(where("foo").gte(12D)).accept(check));
        assertTrue(filter(where("foo").gte(12.5D)).accept(check));
        assertFalse(filter(where("foo").gte(null)).accept(check));
        assertFalse(filter(where("foo").gte(20D)).accept(check));
        assertFalse(filter(where("foo_null").gte(20D)).accept(check));
    }

    @Test
    public void lt_filters_evaluates() throws Exception {
        ObjectNode check = JsonNodeFactory.instance.objectNode();
        check.put("foo", 10.5D);
        check.putNull("foo_null");

        assertTrue(filter(where("foo").lt(12D)).accept(check));
        assertFalse(filter(where("foo").lt(null)).accept(check));
        assertFalse(filter(where("foo").lt(5D)).accept(check));
        assertFalse(filter(where("foo_null").lt(5D)).accept(check));
    }

    @Test
    public void lte_filters_evaluates() throws Exception {
        ObjectNode check = JsonNodeFactory.instance.objectNode();
        check.put("foo", 12.5D);
        check.putNull("foo_null");

        assertTrue(filter(where("foo").lte(13D)).accept(check));
        assertFalse(filter(where("foo").lte(null)).accept(check));
        assertFalse(filter(where("foo").lte(5D)).accept(check));
        assertFalse(filter(where("foo_null").lte(5D)).accept(check));
    }

    @Test
    public void in_filters_evaluates() throws Exception {
        ObjectNode check = JsonNodeFactory.instance.objectNode();
        check.put("item", 3);
        check.putNull("null_item");

        assertTrue(filter(where("item").in(1, 2, 3)).accept(check));
        assertTrue(filter(where("item").in(asList(1, 2, 3))).accept(check));
        assertFalse(filter(where("item").in(4, 5, 6)).accept(check));
        assertFalse(filter(where("item").in(asList(4, 5, 6))).accept(check));
        assertFalse(filter(where("item").in(asList('A'))).accept(check));
        assertFalse(filter(where("item").in(asList((Object) null))).accept(check));

        assertTrue(filter(where("null_item").in((Object) null)).accept(check));
        assertFalse(filter(where("null_item").in(1, 2, 3)).accept(check));
    }

    @Test
    public void nin_filters_evaluates() throws Exception {
        ObjectNode check = JsonNodeFactory.instance.objectNode();
        check.put("item", 3);
        check.putNull("null_item");

        assertTrue(filter(where("item").nin(4, 5)).accept(check));
        assertTrue(filter(where("item").nin(asList(4, 5))).accept(check));
        assertTrue(filter(where("item").nin(asList('A'))).accept(check));
        assertTrue(filter(where("null_item").nin(1, 2, 3)).accept(check));
        assertTrue(filter(where("item").nin(asList((Object) null))).accept(check));

        assertFalse(filter(where("item").nin(3)).accept(check));
        assertFalse(filter(where("item").nin(asList(3))).accept(check));
    }

    @Test
    public void all_filters_evaluates() throws Exception {
        ObjectNode check = JsonNodeFactory.instance.objectNode();
        check.put("items", JsonNodeUtil.asArrayNode(1, 2, 3));

        assertTrue(filter(where("items").all(1, 2, 3)).accept(check));
        assertFalse(filter(where("items").all(1, 2, 3, 4)).accept(check));
    }

    @Test
    public void size_filters_evaluates() throws Exception {
        ObjectNode check = JsonNodeFactory.instance.objectNode();
        check.put("items", JsonNodeUtil.asArrayNode(1, 2, 3));
        check.put("items_empty", JsonNodeFactory.instance.arrayNode());

        assertTrue(filter(where("items").size(3)).accept(check));
        assertTrue(filter(where("items_empty").size(0)).accept(check));
        assertFalse(filter(where("items").size(2)).accept(check));
    }

    @Test
    public void exists_filters_evaluates() throws Exception {
        ObjectNode check = JsonNodeFactory.instance.objectNode();
        check.put("foo", "foo");
        check.putNull("foo_null");

        assertTrue(filter(where("foo").exists(true)).accept(check));
        assertFalse(filter(where("foo").exists(false)).accept(check));

        assertTrue(filter(where("foo_null").exists(true)).accept(check));
        assertFalse(filter(where("foo_null").exists(false)).accept(check));

        assertTrue(filter(where("bar").exists(false)).accept(check));
        assertFalse(filter(where("bar").exists(true)).accept(check));
    }

    @Test
    public void type_filters_evaluates() throws Exception {
        ObjectNode check = JsonNodeFactory.instance.objectNode();
        check.put("string", "foo");
        check.putNull("string_null");
        check.put("int", 1);
        check.put("long", 1L);
        check.put("double", 1.12D);

        assertFalse(filter(where("string_null").type(String.class)).accept(check));
        assertTrue(filter(where("string").type(String.class)).accept(check));
        assertFalse(filter(where("string").type(Number.class)).accept(check));

        assertTrue(filter(where("int").type(Integer.class)).accept(check));
        assertFalse(filter(where("int").type(Long.class)).accept(check));

        assertTrue(filter(where("long").type(Long.class)).accept(check));
        assertFalse(filter(where("long").type(Integer.class)).accept(check));

        assertTrue(filter(where("double").type(Double.class)).accept(check));
        assertFalse(filter(where("double").type(Integer.class)).accept(check));
    }

    @Test
    public void pattern_filters_evaluates() throws Exception {
        ObjectNode check = JsonNodeFactory.instance.objectNode();
        check.put("name", "kalle");
        check.putNull("name_null");

        assertFalse(filter(where("name_null").regex(Pattern.compile(".alle"))).accept(check));
        assertTrue(filter(where("name").regex(Pattern.compile(".alle"))).accept(check));
        assertFalse(filter(where("name").regex(Pattern.compile("KALLE"))).accept(check));
        assertTrue(filter(where("name").regex(Pattern.compile("KALLE", Pattern.CASE_INSENSITIVE))).accept(check));

    }

    // -------------------------------------------------
    //
    // Single filter tests
    //
    // -------------------------------------------------

    @Test
    public void filters_can_be_combined() throws Exception {
        ObjectNode check = JsonNodeFactory.instance.objectNode();
        check.put("string", "foo");
        check.putNull("string_null");
        check.put("int", 10);
        check.put("long", 1L);
        check.put("double", 1.12D);

        Filter shouldMarch = filter(where("string").is("foo").and("int").lt(11));
        Filter shouldNotMarch = filter(where("string").is("foo").and("int").gt(11));

        assertTrue(shouldMarch.accept(check));
        assertFalse(shouldNotMarch.accept(check));
    }

    @Test
    public void filters_can_be_extended_with_new_criteria() throws Exception {
        ObjectNode check = JsonNodeFactory.instance.objectNode();
        check.put("string", "foo");
        check.putNull("string_null");
        check.put("int", 10);
        check.put("long", 1L);
        check.put("double", 1.12D);

        Filter filter = filter(where("string").is("foo").and("int").lt(11));

        assertTrue(filter.accept(check));

        filter.addCriteria(where("long").ne(1L));

        assertFalse(filter.accept(check));

    }

    @Test
    public void filters_criteria_can_be_refined() throws Exception {
        ObjectNode check = JsonNodeFactory.instance.objectNode();
        check.put("string", "foo");
        check.putNull("string_null");
        check.put("int", 10);
        check.put("long", 1L);
        check.put("double", 1.12D);

        Filter filter = filter(where("string").is("foo"));

        assertTrue(filter.accept(check));

        Criteria criteria = where("string").is("not eq");

        filter.addCriteria(criteria);

        assertFalse(filter.accept(check));

        filter = filter(where("string").is("foo").and("string").is("not eq"));
        assertFalse(filter.accept(check));

        filter = filter(where("string").is("foo").and("string").is("foo"));
        assertTrue(filter.accept(check));
    }

    @Test
    public void arrays_of_maps_can_be_filtered() throws Exception {
        ObjectNode rootGrandChild_A = JsonNodeFactory.instance.objectNode();
        rootGrandChild_A.put("name", "rootGrandChild_A");

        ObjectNode rootGrandChild_B = JsonNodeFactory.instance.objectNode();
        rootGrandChild_B.put("name", "rootGrandChild_B");

        ObjectNode rootGrandChild_C = JsonNodeFactory.instance.objectNode();
        rootGrandChild_C.put("name", "rootGrandChild_C");

        ObjectNode rootChild_A = JsonNodeFactory.instance.objectNode();
        rootChild_A.put("name", "rootChild_A");
        rootChild_A.put("children", JsonNodeUtil.asArrayNode(rootGrandChild_A, rootGrandChild_B, rootGrandChild_C));

        ObjectNode rootChild_B = JsonNodeFactory.instance.objectNode();
        rootChild_B.put("name", "rootChild_B");
        rootChild_B.put("children", JsonNodeUtil.asArrayNode(rootGrandChild_A, rootGrandChild_B, rootGrandChild_C));

        ObjectNode rootChild_C = JsonNodeFactory.instance.objectNode();
        rootChild_C.put("name", "rootChild_C");
        rootChild_C.put("children", JsonNodeUtil.asArrayNode(rootGrandChild_A, rootGrandChild_B, rootGrandChild_C));

        ObjectNode root = JsonNodeFactory.instance.objectNode();
        root.put("children", JsonNodeUtil.asArrayNode(rootChild_A, rootChild_B, rootChild_C));

        Filter customFilter = new Filter.FilterAdapter() {
            @Override
            public boolean accept(JsonNode map) {
                if (map.get("name").asText().equals("rootGrandChild_A")) {
                    return true;
                }
                return false;
            }
        };

        Filter rootChildFilter = filter(where("name").regex(Pattern.compile("rootChild_[A|B]")));
        Filter rootGrandChildFilter = filter(where("name").regex(Pattern.compile("rootGrandChild_[A|B]")));

        JsonNode read = JsonPath.read(root, "children[?].children[?][?]", rootChildFilter, rootGrandChildFilter, customFilter);
    }

    @Test
    public void arrays_of_objects_can_be_filtered() throws Exception {
        ObjectNode doc = JsonNodeFactory.instance.objectNode();
        doc.put("items", JsonNodeUtil.asArrayNode(1, 2, 3));

        Filter customFilter = new Filter.FilterAdapter() {
            @Override
            public boolean accept(JsonNode o) {
                return 1 == o.asInt();
            }
        };

        JsonNode res = JsonPath.read(doc, "$.items[?]", customFilter);

        assertEquals(1, res.get(0).asInt());
    }

}
