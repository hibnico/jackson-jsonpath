/*
 * Copyright 2012-2014 the original author or authors.
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

import static com.fasterxml.jackson.jsonpath.jsonassert.JsonAssert.asObject;
import static com.fasterxml.jackson.jsonpath.jsonassert.JsonAssert.isNoValue;
import static com.fasterxml.jackson.jsonpath.jsonassert.JsonAssert.with;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.io.Serializable;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * test defined in http://jsonpath.googlecode.com/svn/trunk/tests/jsonpath-test-js.html
 */
public class ComplianceTest {

    private static ArrayNode jsonTest;

    static {
        try {
            jsonTest = (ArrayNode) new ObjectMapper().readTree(ComplianceTest.class.getResource("jsonpath-test.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test0() throws Exception {
        // @formatter:off
        with(jsonTest.get(0).get("o"))
            .assertThat("$.a", asObject(equalTo("a")))
            .assertThat("$['a']", asObject(equalTo("a")))
            .assertThat("$['c d']", asObject(equalTo("e")))
            .assertThat("$.*", asObject(hasItems("a", "b", "e")))
            .assertThat("$['*']", isNoValue())
            .assertThat("$[*]", asObject(hasItems(hasEntry("a", "a"), hasEntry("b", "b"), hasEntry("c d", "e"))));
        // @formatter:on
    }

    @Test
    public void test1() throws Exception {
        // @formatter:off
        with(jsonTest.get(1).get("o"))
            .assertThat("$[0]", asObject(equalTo(1)))
            .assertThat("$[4]", asObject(equalTo(null)))
            .assertThat("$[*]", asObject(hasItems(1, "2", 3.14d, true, (Serializable) null)))
            .assertThat("$[-1:]", asObject(hasItems(nullValue())));
        // @formatter:on
    }

    @Test
    public void test2() throws Exception {
        // @formatter:off
        with(jsonTest.get(2).get("o"))
            .assertThat("$.points[1]", asObject(allOf(hasEntry("id", "i2"), hasEntry("x", -2), hasEntry("y", 2), hasEntry("z", 1))))
            .assertThat("$.points[4].x", asObject(equalTo(0)))
            .assertThat("$.points[*][?(@.id=='i4')].x", asObject(hasItem(-6)))
            .assertThat("$.points[*].x", asObject(hasItems(4, -2, 8, -6, 0, 1)))
            .assertThat("$['points'][*][?(@.x*@.x+@.y*@.y > 50)].id", asObject(hasItem("i3")))
            .assertThat("$.points[*][?(@.z != null)].id", asObject(hasItems("i2", "i5")))
            .assertThat("$.points[-1].id", asObject(equalTo("i6")));
        // @formatter:on
    }

    @Test
    public void test3() throws Exception {
        // @formatter:off
        with(jsonTest.get(3).get("o"))
            .assertThat("$.menu.items[*][?(@ != null && @.id && !@.label)].id", asObject(hasItems("Open", "Quality", "Pause", "Mute", "Copy", "Help")))
            .assertThat("$.menu.items[*][?(@!= null  && @.label != null && regexpMatch('.*SVG', @.label))].id", asObject(hasItems("CopySVG", "ViewSVG")))
            .assertThat("$.menu.items[*][?(@ == null)]", asObject(hasItems((Integer) null, null, null, null)))
            .assertThat("$..*[?(pos()==3)]", asObject(hasItem(hasEntry("id", "Open"))));
        // @formatter:on
    }

    @Test
    public void test4() throws Exception {
        // @formatter:off
        with(jsonTest.get(4).get("o"))
            .assertThat("$..*[?(typeof(@) == 'array')][0]", asObject(hasItems(1, 5)))
            .assertThat("$.*[-1:]", asObject(hasItems(hasItems(4), hasItems(8))))
            .assertThat("$.*[*][?(pos()%2==0)]", asObject(hasItems(1, 3, 5, 7)));
        // @formatter:on
    }

    @Test
    public void test5() throws Exception {
        // @formatter:off
        with(jsonTest.get(5).get("o"))
            .assertThat("$.*[?(@.color != null)].x", asObject(hasItems(2, 5, 2)))
            .assertThat("$['lin','cir'].color", asObject(hasItems("red", "blue")));
        // @formatter:on
    }

    @Test
    public void test6() throws Exception {
        // @formatter:off
        with(jsonTest.get(6).get("o"))
            .assertThat("$.text[*][?(len(@) > 5)]", asObject(hasItem("world2.0")))
            .assertThat("$.text[*][?(charAt(@, 0) == 'h')]", asObject(hasItem("hello")));
        // @formatter:on
    }

    @Test
    public void test7() throws Exception {
        // @formatter:off
        with(jsonTest.get(7).get("o"))
            .assertThat("$..*.a", asObject(hasItem(6)));
        // @formatter:on
    }

    @Test
    public void test8() throws Exception {
        // @formatter:off
        with(jsonTest.get(8).get("o"))
            .assertThat("$.a[*][?(@['\\@']==3)].a", asObject(hasItem(6)))
            .assertThat("$.a[*][?(@['$']==5)].a", asObject(hasItem(7)));
        // @formatter:on
    }

}
