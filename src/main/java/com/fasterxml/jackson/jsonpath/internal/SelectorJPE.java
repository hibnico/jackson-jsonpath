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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.jsonpath.JsonPathRuntimeException;
import com.fasterxml.jackson.jsonpath.JsonPathValue;

class SelectorJPE extends JsonPathExpression {

    private JsonPathExpression object;

    private JsonPathExpression index;

    SelectorJPE(int position, JsonPathExpression object, String id) {
        this(position, object, new LiteralJPE(position, JsonNodeFactory.instance.textNode(id)));
    }

    SelectorJPE(int position, JsonPathExpression object, int index) {
        this(position, object, new LiteralJPE(position, JsonNodeFactory.instance.numberNode(index)));
    }

    SelectorJPE(int position, JsonPathExpression object, JsonPathExpression index) {
        super(position);
        this.object = object;
        this.index = index;
    }

    @Override
    boolean isVector() {
        return object.isVector();
    }

    @Override
    public JsonPathValue eval(JsonPathContext context) {
        return evalAsDotProduct(context, object);
    }

    @Override
    Object computeObject(JsonPathContext context, JsonNode[] childValues) {
        JsonNode o = childValues[0];
        JsonNode i = index.eval(context).toNode();

        if (o.isObject()) {
            String si = asLenientString(i);
            if (si != null) {
                return o.get(si);
            }
            return o.get(asInt(i, "index of selector"));
        }
        if (o.isArray()) {
            return o.get(asInt(i, "index of selector"));
        }
        throw new JsonPathRuntimeException("the json selector cannot be applied to "
                + o.getNodeType().toString().toLowerCase(), position);
    }

    @Override
    public String toString() {
        return object.toString() + "[(" + index.toString() + ")]";
    }
}
