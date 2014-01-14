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

import com.fasterxml.jackson.databind.JsonNode;

public class TernaryJSExpr extends JSExpr {

    private JSExpr condition;

    private JSExpr onTrue;

    private JSExpr onFalse;

    public TernaryJSExpr(JSExpr condition, JSExpr onTrue, JSExpr onFalse) {
        this.condition = condition;
        this.onTrue = onTrue;
        this.onFalse = onFalse;
    }

    @Override
    public Object eval(JsonNode node) {
        boolean c = asBoolean(condition.eval(node));
        if (c) {
            return onTrue.eval(node);
        } else {
            return onFalse.eval(node);
        }
    }

    @Override
    public String toString() {
        return condition.toString() + " ? " + onTrue.toString() + " : " + onFalse.toString();
    }
}
