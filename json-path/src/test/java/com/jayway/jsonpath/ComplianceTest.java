package com.jayway.jsonpath;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * test defined in http://jsonpath.googlecode.com/svn/trunk/tests/jsonpath-test-js.html
 */
public class ComplianceTest {

    @SuppressWarnings("unchecked")
    private <T> void checkList(String json, String jsonPath, Matcher<Iterable<T>> matcher) {
        assertThat((List<T>) JsonNodeUtil.asList(JsonPath.read(json, jsonPath)), matcher);
    }

    @Test
    public void test_one() throws Exception {
        // @formatter:off
        String json = "{ \"a\": \"a\",\n"
                + "      \"b\": \"b\",\n"
                + "      \"c d\": \"e\" \n"
                + "    }";
        // @formatter:on

        assertThat(JsonPath.read(json, "$.a").asText(), equalTo("a"));
        checkList(json, "$.*", hasItems("a", "b", "e"));
        checkList(json, "$['*']", hasItems("a", "b", "e"));
        assertThat(JsonPath.read(json, "$['a']").asText(), equalTo("a"));

        // assertThat(JsonPath.<String>read(json, "$.'c d'"), is(equalTo("e"))); //low
        // assertThat(JsonPath.<List<String>>read(json, "$[*]"), hasItems("a", "b", "e")); //low

    }

    @Test
    public void test_two() throws Exception {
        String json = "[ 1, \"2\", 3.14, true, null ]";

        assertThat(JsonPath.read(json, "$[0]").asInt(), is(equalTo(1)));
        assertThat(JsonPath.read(json, "$[4]").isNull(), is(equalTo(true)));
        checkList(json, "$[*]", hasItems(new Integer(1), new String("2"), new Double(3.14), new Boolean(true), (Comparable) null));
        assertThat(JsonPath.read(json, "$[-1:]").isNull(), is(equalTo(true)));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void test_three() throws Exception {
        // @formatter:off
        String json = "{ \"points\": [\n"
                + "        { \"id\": \"i1\", \"x\":  4, \"y\": -5 },\n"
                + "        { \"id\": \"i2\", \"x\": -2, \"y\":  2, \"z\": 1 },\n"
                + "        { \"id\": \"i3\", \"x\":  8, \"y\":  3 },\n"
                + "        { \"id\": \"i4\", \"x\": -6, \"y\": -1 },\n"
                + "        { \"id\": \"i5\", \"x\":  0, \"y\":  2, \"z\": 1 },\n"
                + "        { \"id\": \"i6\", \"x\":  1, \"y\":  4 }\n"
                + "      ]\n"
                + "    }";
        // @formatter:on

        assertThat(
                (Map< ? extends String, ? extends Comparable>) JsonNodeUtil.asMap(JsonPath.read(json, "$.points[1]")),
                allOf(Matchers.<String, Comparable> hasEntry("id", "i2"), Matchers.<String, Comparable> hasEntry("x", -2),
                        Matchers.<String, Comparable> hasEntry("y", 2), Matchers.<String, Comparable> hasEntry("z", 1)));

        assertThat(JsonPath.read(json, "$.points[4].x").asInt(), equalTo(0));

        assertThat((List< ? super Integer>) JsonNodeUtil.asList(JsonPath.read(json, "$.points[?(@.id == 'i4')].x")), hasItem(-6));

        checkList(json, "$.points[*].x", hasItems(4, -2, 8, -6, 0, 1));

        checkList(json, "$.points[?(@.z)].id", hasItems("i2", "i5"));

        assertThat(JsonPath.read(json, "$.points[(@.length - 1)].id").asText(), equalTo("i6"));

        // assertThat(JsonPath.<List<Integer>>read(json, "$['points'][?(@.x * @.x + @.y * @.y > 50)].id"), hasItems(?)); //low
    }

    @SuppressWarnings("UnusedAssignment")
    @Test
    public void test_four() throws Exception {
        // @formatter:off
        String json = "{ \"menu\": {\n" +
                "                 \"header\": \"SVG Viewer\",\n" +
                "                 \"items\": [\n" +
                "                     {\"id\": \"Open\"},\n" +
                "                     {\"id\": \"OpenNew\", \"label\": \"Open New\"},\n" +
                "                     null,\n" +
                "                     {\"id\": \"ZoomIn\", \"label\": \"Zoom In\"},\n" +
                "                     {\"id\": \"ZoomOut\", \"label\": \"Zoom Out\"},\n" +
                "                     {\"id\": \"OriginalView\", \"label\": \"Original View\"},\n" +
                "                     null,\n" +
                "                     {\"id\": \"Quality\"},\n" +
                "                     {\"id\": \"Pause\"},\n" +
                "                     {\"id\": \"Mute\"},\n" +
                "                     null,\n" +
                "                     {\"id\": \"Find\", \"label\": \"Find...\"},\n" +
                "                     {\"id\": \"FindAgain\", \"label\": \"Find Again\"},\n" +
                "                     {\"id\": \"Copy\"},\n" +
                "                     {\"id\": \"CopyAgain\", \"label\": \"Copy Again\"},\n" +
                "                     {\"id\": \"CopySVG\", \"label\": \"Copy SVG\"},\n" +
                "                     {\"id\": \"ViewSVG\", \"label\": \"View SVG\"},\n" +
                "                     {\"id\": \"ViewSource\", \"label\": \"View Source\"},\n" +
                "                     {\"id\": \"SaveAs\", \"label\": \"Save As\"},\n" +
                "                     null,\n" +
                "                     {\"id\": \"Help\"},\n" +
                "                     {\"id\": \"About\", \"label\": \"About Adobe CVG Viewer...\"}\n" +
                "                 ]\n" +
                "               }\n" +
                "             }";
        // @formatter:on

        // assertThat(JsonPath.<List<String>>read(json, "$.menu.items[?(@ && @.id && !@.label)].id"), hasItems("?")); //low
        // assertThat(JsonPath.<List<String>>read(json, "$.menu.items[?(@ && @.label && /SVG/.test(@.label))].id"), hasItems("?")); //low
        // assertThat(JsonPath.<List<String>>read(json, "$.menu.items[?(!@)]"), hasItems("?")); //low
        // assertThat(JsonPath.<List<String>>read(json, "$..[0]"), hasItems("?")); //low

    }

    // @formatter:off
    /*
    --one
    { "o": { a: "a",
               b: "b",
               "c d": "e"
             },
        "p": [ "$.a",
               "$['a']",
               "$.'c d'",
               "$.*",
               "$['*']" ,
               "$[*]"
             ]
      },
      --two
      { "o": [ 1, "2", 3.14, true, null ],
        "p": [ "$[0]",
               "$[4]",
               "$[*]",
    	   "$[-1:]"
             ]
      },
      --three
      { "o": { points: [
                 { id: "i1", x:  4, y: -5 },
                 { id: "i2", x: -2, y:  2, z: 1 },
                 { id: "i3", x:  8, y:  3 },
                 { id: "i4", x: -6, y: -1 },
                 { id: "i5", x:  0, y:  2, z: 1 },
                 { id: "i6", x:  1, y:  4 }
               ]
             },
        "p": [ "$.points[1]",
               "$.points[4].x",
               "$.points[?(@.id=='i4')].x",
               "$.points[*].x",
               "$['points'][?(@.x*@.x+@.y*@.y > 50)].id",
               "$.points[?(@.z)].id",
               "$.points[(@.length-1)].id"
             ]
      },
      --four
      { "o": { "menu": {
                 "header": "SVG Viewer",
                 "items": [
                     {"id": "Open"},
                     {"id": "OpenNew", "label": "Open New"},
                     null,
                     {"id": "ZoomIn", "label": "Zoom In"},
                     {"id": "ZoomOut", "label": "Zoom Out"},
                     {"id": "OriginalView", "label": "Original View"},
                     null,
                     {"id": "Quality"},
                     {"id": "Pause"},
                     {"id": "Mute"},
                     null,
                     {"id": "Find", "label": "Find..."},
                     {"id": "FindAgain", "label": "Find Again"},
                     {"id": "Copy"},
                     {"id": "CopyAgain", "label": "Copy Again"},
                     {"id": "CopySVG", "label": "Copy SVG"},
                     {"id": "ViewSVG", "label": "View SVG"},
                     {"id": "ViewSource", "label": "View Source"},
                     {"id": "SaveAs", "label": "Save As"},
                     null,
                     {"id": "Help"},
                     {"id": "About", "label": "About Adobe CVG Viewer..."}
                 ]
               }
             },
        "p": [ "$.menu.items[?(@ && @.id && !@.label)].id",
               "$.menu.items[?(@ && @.label && /SVG/.test(@.label))].id",
               "$.menu.items[?(!@)]",
    		   "$..[0]"
             ]
      },
      --five
      { "o": { a: [1,2,3,4],
               b: [5,6,7,8]
             },
        "p": [ "$..[0]",
    	       "$..[-1:]",
    		   "$..[?(@%2==0)]"
             ]
      },
      { "o": { lin: {color:"red", x:2, y:3},
               cir: {color:"blue", x:5, y:2, r:1 },
               arc: {color:"green", x:2, y:4, r:2, phi0:30, dphi:120 },
               pnt: {x:0, y:7 }
             },
        "p": [ "$.'?(@.color)'.x",
               "$['lin','cir'].color"
             ]
      },
      { "o": { lin: {color:"red", x:2, y:3},
               cir: {color:"blue", x:5, y:2, r:1 },
               arc: {color:"green", x:2, y:4, r:2, phi0:30, dphi:120 },
               pnt: {x:0, y:7 }
             },
        "p": [ "$.'?(@.color)'.x",
               "$['lin','arc'].color"
             ]
      },
      { "o": { text: [ "hello", "world2.0"] },
        "p": [ "$.text[?(@.length > 5)]",
               "$.text[?(@.charAt(0) == 'h')]"
             ]
      },
      { "o": { a: { a:2, b:3 },
               b: { a:4, b:5 },
               c: { a: { a:6, b:7}, c:8}
             },
        "p": [ "$..a"
             ]
      },
      { "o": { a: [ { a:5, '@':2, '$':3 },   // issue 7: resolved by escaping the '@' character
                    { a:6, '@':3, '$':4 },   // in a JSONPath expression.
                    { a:7, '@':4, '$':5 }
                  ]
             },
        "p": [ "$.a[?(@['\\@']==3)]",
               "$.a[?(@['$']==5)]"
             ]
      }
     */
    // @formatter:on
}
