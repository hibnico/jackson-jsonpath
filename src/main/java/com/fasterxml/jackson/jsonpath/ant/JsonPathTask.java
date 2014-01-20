/*
 * Copyright 2012-2014 the original author or authors.
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
package com.fasterxml.jackson.jsonpath.ant;

import java.io.IOException;
import java.text.ParseException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.jsonpath.JsonPath;

public class JsonPathTask extends Task {

    private String property;

    private String json;

    private String path;

    public void setProperty(String property) {
        this.property = property;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void execute() throws BuildException {
        if (property == null) {
            throw new BuildException("missing 'property' attribute");
        }
        if (json == null) {
            throw new BuildException("missing 'json' attribute");
        }
        if (path == null) {
            throw new BuildException("missing 'path' attribute");
        }
        JsonPath jsonPath;
        try {
            jsonPath = JsonPath.compile(path);
        } catch (ParseException e) {
            throw new BuildException("Invalid json path: " + e.getMessage(), e);
        }
        JsonNode result;
        try {
            result = jsonPath.eval(json).asNode();
        } catch (JsonProcessingException e) {
            throw new BuildException("Invalid json: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new BuildException("Invalid json: " + e.getMessage(), e);
        }
        if (result.isNull()) {
            log("Nothing matched, " + property + " not set", Project.MSG_VERBOSE);
        } else if (result.isContainerNode() && result.size() == 0) {
            log("Empty container matched, " + property + " not set", Project.MSG_VERBOSE);
        } else {
            if (result.size() == 1) {
                result = result.iterator().next();
                log("Container with 1 element matched, selecting that element", Project.MSG_VERBOSE);
            }
            String value;
            if (result.isTextual()) {
                value = result.asText();
            } else {
                value = result.toString();
            }
            log("Setting '" + property + "' to: " + value, Project.MSG_VERBOSE);
            getProject().setNewProperty(property, value);
        }
    }
}
