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
package com.fasterxml.jackson.jsonpath.internal;

import java.text.ParseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.jsonpath.JsonPathRuntimeException;
import com.fasterxml.jackson.jsonpath.JsonPathValue;

class IndexRangeSelectorJPE extends JsonPathExpression {

    private JsonPathExpression object;

    private int start;

    private Integer end;

    private int step;

    IndexRangeSelectorJPE(int position, JsonPathExpression object, int start, Integer end, Integer step)
            throws ParseException {
        super(position, object.isVector());
        this.object = object;
        this.start = start;
        this.end = end;
        this.step = step == null ? 1 : step;
        if (this.step == 0) {
            throw new ParseException("the step in a range must be different from 0", position);
        }
    }

    @Override
    public JsonPathValue eval(JsonPathContext context) {
        return evalAsDotProduct(context, object);
    }

    @Override
    JsonNode computeNode(JsonPathContext context, JsonNode[] childValues) {
        JsonNode o = childValues[0];
        if (!o.isArray()) {
            throw new JsonPathRuntimeException("index selector must apply on an array, not a "
                    + o.getNodeType().toString().toLowerCase(), position);
        }
        ArrayNode ret = JsonNodeFactory.instance.arrayNode();
        int s = start >= 0 ? start : o.size() + start;
        int e = end != null ? end : (step > 0 ? o.size() - 1 : 0);
        if (step > 0) {
            if (s > e) {
                throw new JsonPathRuntimeException("start is greater than end in the range", position);
            }
            for (int i = s; i <= e; i += step) {
                ret.add(o.get(i));
            }
        } else {
            if (s < e) {
                throw new JsonPathRuntimeException("start is lower than end in the reversed range", position);
            }
            for (int i = s; i >= e; i += step) {
                ret.add(o.get(i));
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        return object.toString() + "[" + start + ":" + (end == null ? "" : end) + ":" + step + "]";
    }
}
