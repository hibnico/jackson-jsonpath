/*
 * Copyright 2014 the original author or authors.
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
package com.fasterxml.jackson.jsonpath.internal.js;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ArraySelectorJSExpr extends JSExpr {

    private JSExpr array;

    private JSExpr index;

    public ArraySelectorJSExpr(JSExpr array, JSExpr index) {
        this.array = array;
        this.index = index;
    }

    @Override
    public Object eval(JsonNode node) {
        Object a = array.eval(node);
        Object i = index.eval(node);

        if (a instanceof ObjectNode) {
            String si = asLenientString(i);
            if (si != null) {
                return ((ObjectNode) a).get(si);
            }
            return ((ObjectNode) a).get(asInt(i));
        }
        if (a instanceof ArrayNode) {
            return ((ArrayNode) a).get(asInt(i));
        }
        if (a instanceof List) {
            return ((List<?>) a).get(asInt(i));
        }
        if (a instanceof Map) {
            return ((Map<?, ?>) a).get(i);
        }
        throw new IllegalStateException("expecting an array: " + a);
    }
    
    @Override
    public String toString() {
        return array.toString() + "[" + index.toString() + "]";
    }
}
