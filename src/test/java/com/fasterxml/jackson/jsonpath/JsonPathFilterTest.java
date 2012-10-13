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

import static com.fasterxml.jackson.jsonpath.Criteria.where;
import static com.fasterxml.jackson.jsonpath.Filter.filter;
import static junit.framework.Assert.assertEquals;

import java.util.regex.Pattern;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.jsonpath.Filter;
import com.fasterxml.jackson.jsonpath.JsonNodeUtil;
import com.fasterxml.jackson.jsonpath.JsonPath;
import com.fasterxml.jackson.jsonpath.Filter.FilterAdapter;

public class JsonPathFilterTest {

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

        JsonPath.read(root, "children[?].children[?][?]", rootChildFilter, rootGrandChildFilter, customFilter);
    }

    @Test
    public void arrays_of_objects_can_be_filtered() throws Exception {
        ObjectNode doc = JsonNodeFactory.instance.objectNode();
        doc.put("items", JsonNodeUtil.asArrayNode(1, 2, 3));

        FilterAdapter customFilter = new Filter.FilterAdapter() {
            @Override
            public boolean accept(JsonNode o) {
                return 1 == o.asInt();
            }
        };

        JsonNode res = JsonPath.read(doc, "$.items[?]", customFilter);

        assertEquals(1, res.get(0).asInt());
    }

}
