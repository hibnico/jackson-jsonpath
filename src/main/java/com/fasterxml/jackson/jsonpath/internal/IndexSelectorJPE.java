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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPathNoValue;
import com.fasterxml.jackson.jsonpath.JsonPathRuntimeException;
import com.fasterxml.jackson.jsonpath.JsonPathSingleValue;
import com.fasterxml.jackson.jsonpath.JsonPathValue;

class IndexSelectorJPE extends JsonPathExpression {

    private JsonPathExpression object;

    private int index;

    IndexSelectorJPE(int position, JsonPathExpression object, int index) {
        super(position, true);
        this.object = object;
        this.index = index;
    }

    @Override
    public JsonPathValue eval(JsonPathContext context) {
        JsonNode o = object.eval(context).toNode();
        if (!o.isArray()) {
            throw new JsonPathRuntimeException("index selector must apply on an array, not a "
                    + o.getNodeType().toString().toLowerCase(), position);
        }
        JsonNode res = o.get(index);
        if (res == null) {
            return JsonPathNoValue.INSTANCE;
        }
        return new JsonPathSingleValue(res);
    }

    @Override
    public String toString() {
        return object.toString() + "[" + index + "]";
    }
}
