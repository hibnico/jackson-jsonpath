package com.jayway.jsonpath;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;

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
