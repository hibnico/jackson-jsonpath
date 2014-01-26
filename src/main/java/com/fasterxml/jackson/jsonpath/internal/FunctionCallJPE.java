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
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPathFunction;
import com.fasterxml.jackson.jsonpath.JsonPathValue;

class FunctionCallJPE extends JsonPathExpression {

    private JsonPathFunction function;

    private List<JsonPathExpression> arguments;

    FunctionCallJPE(int position, JsonPathFunction function, List<JsonPathExpression> arguments) throws ParseException {
        super(position, function.isVector());
        this.function = function;
        this.arguments = arguments;
    }

    @Override
    public JsonPathValue eval(JsonPathContext context) {
        List<JsonNode> args = new ArrayList<JsonNode>();
        for (JsonPathExpression e : arguments) {
            args.add(e.eval(context).asNode());
        }
        return function.call(context, args);
    }
}
