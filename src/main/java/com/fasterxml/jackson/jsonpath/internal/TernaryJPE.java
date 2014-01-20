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

import com.fasterxml.jackson.jsonpath.JsonPathValue;

class TernaryJPE extends JsonPathExpression {

    private JsonPathExpression condition;

    private JsonPathExpression onTrue;

    private JsonPathExpression onFalse;

    TernaryJPE(int position, JsonPathExpression condition, JsonPathExpression onTrue, JsonPathExpression onFalse)
            throws ParseException {
        super(position, onTrue.isVector());
        this.condition = condition;
        this.onTrue = onTrue;
        this.onFalse = onFalse;
        if (onTrue.isVector() != onFalse.isVector()) {
            throw new ParseException("Incompatible dimension of onTrue and onFalse", position);
        }
    }

    @Override
    public JsonPathValue eval(JsonPathContext context) {
        boolean c = condition.evalAsBoolean(context);
        if (c) {
            return onTrue.eval(context);
        } else {
            return onFalse.eval(context);
        }
    }

    @Override
    public String toString() {
        return condition.toString() + " ? " + onTrue.toString() + " : " + onFalse.toString();
    }
}
