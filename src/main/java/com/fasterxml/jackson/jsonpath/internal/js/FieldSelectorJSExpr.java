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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FieldSelectorJSExpr extends JSExpr {

    private JSExpr object;

    private String field;

    public FieldSelectorJSExpr(JSExpr object, String field) {
        this.object = object;
        this.field = field;
    }

    @Override
    public Object eval(JsonNode node) {
        Object o = object.eval(node);
        if (o == null) {
            throw new IllegalStateException("NPE");
        }
        if (o instanceof ObjectNode) {
            return ((ObjectNode) o).get(field);
        }
        if (o instanceof JsonNode) {
            throw new IllegalStateException("non json object " + o.getClass());
        }
        Method getter = null;
        try {
            getter = o.getClass().getMethod("get" + Character.toUpperCase(field.charAt(0)) + field.substring(1));
        } catch (SecurityException e) {
            throw new IllegalStateException(e);
        } catch (NoSuchMethodException e) {
            // no such getter
        }
        if (getter == null) {
            try {
                getter = o.getClass().getMethod("is" + Character.toUpperCase(field.charAt(0)) + field.substring(1));
            } catch (SecurityException e) {
                throw new IllegalStateException(e);
            } catch (NoSuchMethodException e) {
                // no such getter
            }
        }
        if (getter != null) {
            try {
                return getter.invoke(o);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }
        Field f = null;
        try {
            f = o.getClass().getField(field);
        } catch (SecurityException e) {
            throw new IllegalStateException(e);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
        try {
            return f.get(o);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public String toString() {
        return object.toString() + "." + field;
    }
}
