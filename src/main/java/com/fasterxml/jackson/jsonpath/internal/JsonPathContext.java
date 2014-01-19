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

public class JsonPathContext {

    private JsonNode root;

    private JsonNode this_;

    private Integer pos;

    public JsonPathContext(JsonNode root) {
        this(root, root);
    }

    JsonPathContext(JsonPathContext context, JsonNode this_, int pos) {
        this(context.root, this_);
        this.pos = pos;
    }

    private JsonPathContext(JsonNode root, JsonNode this_) {
        this.root = root;
        this.this_ = this_;
    }

    public JsonNode getRoot() {
        return root;
    }

    public JsonNode getThis() {
        return this_;
    }

    public Integer getPos() {
        return pos;
    }
}
