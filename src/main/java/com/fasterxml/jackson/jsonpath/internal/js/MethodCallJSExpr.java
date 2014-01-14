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

import com.fasterxml.jackson.databind.JsonNode;

public class MethodCallJSExpr extends JSExpr {

    private JSExpr object;

    private String function;

    private List<JSExpr> arguments;

    public MethodCallJSExpr(JSExpr object, String function, List<JSExpr> arguments) {
        this.object = object;
        this.function = function;
        this.arguments = arguments;
    }

    @Override
    public Object eval(JsonNode node) {
        throw new IllegalStateException("TODO");
    }
}
