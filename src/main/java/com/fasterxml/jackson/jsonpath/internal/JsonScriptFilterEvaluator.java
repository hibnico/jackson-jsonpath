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
import com.fasterxml.jackson.jsonpath.JsonPathSingleValue;
import com.fasterxml.jackson.jsonpath.JsonPathValue;
import com.fasterxml.jackson.jsonpath.internal.js.JSExpr;

class JsonScriptFilterEvaluator extends JsonPathEvaluator {

    private JSExpr expression;

    public JsonScriptFilterEvaluator(JSExpr expression) {
        this.expression = expression;
    }

    @Override
    public JsonPathValue eval(JsonNode node) {
        boolean select = expression.evalBoolean(node);
        if (select) {
            return new JsonPathSingleValue(node);
        }
        return JsonPathNoValue.INSTANCE;
    }

}
