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
import com.fasterxml.jackson.jsonpath.JsonPathSingleValue;
import com.fasterxml.jackson.jsonpath.JsonPathValue;
import com.fasterxml.jackson.jsonpath.JsonPathVectorValue;

class FilterJPE extends JsonPathExpression {

    private JsonPathExpression object;

    private JsonPathExpression filter;

    FilterJPE(int position, JsonPathExpression object, JsonPathExpression filter) {
        super(position, object.isVector());
        this.object = object;
        this.filter = filter;
    }

    @Override
    public JsonPathValue eval(JsonPathContext context) {
        JsonPathValue value = object.eval(context);
        if (isVector()) {
            JsonPathVectorValue ret = new JsonPathVectorValue();
            int i = 0;
            for (JsonNode subNode : value.getNodes()) {
                String name = ((JsonPathVectorValue) value).getName(i);
                boolean select = filter.evalAsBoolean(new JsonPathContext(context, subNode, i, name));
                if (select) {
                    ret.add(subNode, name);
                }
                i++;
            }
            return ret;
        }
        boolean select = filter.evalAsBoolean(new JsonPathContext(context, value.asNode(), 0, null));
        if (select) {
            return value;
        }
        return JsonPathSingleValue.EMPTY;
    }

    @Override
    public String toString() {
        return object.toString() + "[?(" + filter.toString() + ")]";
    }
}
