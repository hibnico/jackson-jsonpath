/*
 * Copyright 2012 the original author or authors.
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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

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
		JsonPath jsonPath = JsonPath.compile(path);
		JsonNode result = jsonPath.read(json);
		if (result != null && result.size() != 0) {
			if (result.size() == 1) {
				result = result.iterator().next();
			}
			getProject().setNewProperty(property, result.toString());
		}
	}
}
