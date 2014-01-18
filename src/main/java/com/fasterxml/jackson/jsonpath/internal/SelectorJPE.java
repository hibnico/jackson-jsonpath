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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

class SelectorJPE extends JsonPathExpression {

    private JsonPathExpression index;

    SelectorJPE(JsonPathExpression object, String id) {
        this(object, new LiteralJPE(JsonNodeFactory.instance.textNode(id)));
    }

    SelectorJPE(JsonPathExpression object, int index) {
        this(object, new LiteralJPE(JsonNodeFactory.instance.numberNode(index)));
    }

    SelectorJPE(JsonPathExpression object, JsonPathExpression index) {
        super(object);
        this.index = index;
    }

    @Override
    Object computeObject(JsonPathContext context, JsonNode[] childValues) {
        JsonNode o = childValues[0];
        JsonNode i = index.eval(context).toNode();

        if (o instanceof ObjectNode) {
            String si = asLenientString(i);
            if (si != null) {
                return ((ObjectNode) o).get(si);
            }
            return ((ObjectNode) o).get(asInt(i));
        }
        if (o instanceof ArrayNode) {
            return ((ArrayNode) o).get(asInt(i));
        }
        throw new IllegalStateException("unsupported object type: " + o.getClass());
    }

    @Override
    public String toString() {
        return children[0].toString() + "[(" + index.toString() + ")]";
    }
}
