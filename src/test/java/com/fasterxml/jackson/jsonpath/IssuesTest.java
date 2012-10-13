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

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPath;

public class IssuesTest {

    @Test
    public void issue_7() throws Exception {
        String json = "{ \"foo\" : [\n" + "  { \"id\": 1 },  \n" + "  { \"id\": 2 },  \n" + "  { \"id\": 3 }\n" + "  ] }";
        assertNull(JsonPath.read(json, "$.foo.id"));
    }

    @Test
    public void issue_11() throws Exception {
        String json = "{ \"foo\" : [] }";
        JsonNode result = JsonPath.read(json, "$.foo[?(@.rel= 'item')][0].uri");
        assertTrue(result.size() == 0);
    }

}
