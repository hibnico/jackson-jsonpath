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
package com.jayway.jsonpath;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.jayway.jsonpath.internal.PathToken;
import com.jayway.jsonpath.internal.PathTokenizer;
import com.jayway.jsonpath.internal.filter.PathTokenFilter;

/**
 * <p/>
 * JsonPath is to JSON what XPATH is to XML, a simple way to extract parts of a given document. JsonPath is available in many programming languages
 * such as Javascript, Python and PHP.
 * <p/>
 * JsonPath allows you to compile a json path string to use it many times or to compile and apply in one single on demand operation.
 * <p/>
 * Given the Json document:
 * <p/>
 * <code>
 * String json =
 * "{
 * "store":
 * {
 * "book":
 * [
 * {
 * "category": "reference",
 * "author": "Nigel Rees",
 * "title": "Sayings of the Century",
 * "price": 8.95
 * },
 * {
 * "category": "fiction",
 * "author": "Evelyn Waugh",
 * "title": "Sword of Honour",
 * "price": 12.99
 * }
 * ],
 * "bicycle":
 * {
 * "color": "red",
 * "price": 19.95
 * }
 * }
 * }";
 * </code>
 * <p/>
 * A JsonPath can be compiled and used as shown:
 * <p/>
 * <code>
 * JsonPath path = JsonPath.compile("$.store.book[1]");
 * <br/>
 * List&lt;Object&gt; books = path.read(json);
 * </code>
 * </p>
 * Or:
 * <p/>
 * <code>
 * List&lt;Object&gt; authors = JsonPath.read(json, "$.store.book[*].author")
 * </code>
 * <p/>
 * If the json path returns a single value (is definite):
 * </p>
 * <code>
 * String author = JsonPath.read(json, "$.store.book[1].author")
 * </code>
 */
public class JsonPath {

    private static Pattern DEFINITE_PATH_PATTERN = Pattern.compile(".*(\\.\\.|\\*|\\[[\\\\/]|\\?|,|:\\s?\\]|\\[\\s?:|>|\\(|<|=|\\+).*");

    private ObjectMapper jsonMapper = new ObjectMapper();
    private PathTokenizer tokenizer;
    private LinkedList<Filter> filters;

    public JsonPath(String jsonPath, Filter[] filters) {
        if (jsonPath == null || jsonPath.trim().length() == 0 || jsonPath.matches("[^\\?\\+\\=\\-\\*\\/\\!]\\(")) {

            throw new InvalidPathException("Invalid path");
        }

        int filterCountInPath = StringUtils.countMatches(jsonPath, "[?]");
        isTrue(filterCountInPath == filters.length, "Filters in path ([?]) does not match provided filters.");

        this.tokenizer = new PathTokenizer(jsonPath);
        this.filters = new LinkedList<Filter>();
        this.filters.addAll(asList(filters));

    }

    PathTokenizer getTokenizer() {
        return this.tokenizer;
    }

    public JsonPath copy() {
        return new JsonPath(tokenizer.getPath(), filters.toArray(new Filter[0]));
    }

    /**
     * Returns the string representation of this JsonPath
     * 
     * @return path as String
     */
    public String getPath() {
        return this.tokenizer.getPath();
    }

    /**
     * Checks if a path points to a single item or if it potentially returns multiple items
     * <p/>
     * a path is considered <strong>not</strong> definite if it contains a scan fragment ".." or an array position fragment that is not based on a
     * single index
     * <p/>
     * <p/>
     * definite path examples are:
     * <p/>
     * $store.book $store.book[1].title
     * <p/>
     * not definite path examples are:
     * <p/>
     * $..book $.store.book[1,2] $.store.book[?(@.category = 'fiction')]
     * 
     * @return true if path is definite (points to single item)
     */
    public boolean isPathDefinite() {
        String preparedPath = getPath().replaceAll("\"[^\"\\\\\\n\r]*\"", "");

        return !DEFINITE_PATH_PATTERN.matcher(preparedPath).matches();
    }

    /**
     * Applies this JsonPath to the provided json document. Note that the document must either a {@link List} or a {@link Map}
     * 
     * @param jsonObject a container Object ({@link List} or {@link Map})
     * @param <T> expected return type
     * @return list of objects matched by the given path
     */
    public JsonNode read(JsonNode node) {
        notNull(node, "json can not be null");

        LinkedList<Filter> contextFilters = new LinkedList<Filter>(filters);

        boolean inArrayContext = false;

        for (PathToken pathToken : tokenizer) {
            PathTokenFilter filter = pathToken.getFilter();
            node = filter.filter(node, contextFilters, inArrayContext);
            if (!inArrayContext) {
                inArrayContext = filter.isArrayFilter();
            }
        }
        return node;
    }

    /**
     * Applies this JsonPath to the provided json string
     * 
     * @param json a json string
     * @param <T> expected return type
     * @return list of objects matched by the given path
     */
    public JsonNode read(String json) {
        notEmpty(json, "json can not be null or empty");
        try {
            return read(jsonMapper.readTree(json));
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    /**
     * Applies this JsonPath to the provided json URL
     * 
     * @param jsonURL url to read from
     * @param <T> expected return type
     * @return list of objects matched by the given path
     * @throws IOException
     */
    public JsonNode read(URL jsonURL) throws IOException {
        notNull(jsonURL, "json URL can not be null");
        return read(jsonMapper.readTree(jsonURL));
    }

    /**
     * Applies this JsonPath to the provided json file
     * 
     * @param jsonFile file to read from
     * @param <T> expected return type
     * @return list of objects matched by the given path
     * @throws IOException
     */
    public JsonNode read(File jsonFile) throws IOException {
        notNull(jsonFile, "json file can not be null");
        isTrue(jsonFile.exists(), "json file does not exist");
        return read(jsonMapper.readTree(jsonFile));
    }

    /**
     * Applies this JsonPath to the provided json input stream
     * 
     * @param jsonInputStream input stream to read from
     * @param <T> expected return type
     * @return list of objects matched by the given path
     * @throws IOException
     */
    public JsonNode read(InputStream jsonInputStream) throws IOException {
        notNull(jsonInputStream, "json input stream can not be null");
        return read(jsonMapper.readTree(jsonInputStream));
    }

    // --------------------------------------------------------
    //
    // Static factory methods
    //
    // --------------------------------------------------------

    /**
     * Compiles a JsonPath
     * 
     * @param jsonPath to compile
     * @param filters filters to be applied to the filter place holders [?] in the path
     * @return compiled JsonPath
     */
    public static JsonPath compile(String jsonPath, Filter... filters) {
        notEmpty(jsonPath, "json can not be null or empty");

        return new JsonPath(jsonPath, filters);
    }

    // --------------------------------------------------------
    //
    // Static utility functions
    //
    // --------------------------------------------------------

    /**
     * Creates a new JsonPath and applies it to the provided Json string
     * 
     * @param json a json string
     * @param jsonPath the json path
     * @param filters filters to be applied to the filter place holders [?] in the path
     * @param <T> expected return type
     * @return list of objects matched by the given path
     */
    public static JsonNode read(String json, String jsonPath, Filter... filters) {
        notEmpty(json, "json can not be null or empty");
        notEmpty(jsonPath, "jsonPath can not be null or empty");
        return compile(jsonPath, filters).read(json);
    }

    /**
     * Creates a new JsonPath and applies it to the provided Json object
     * 
     * @param json a json object
     * @param jsonPath the json path
     * @param filters filters to be applied to the filter place holders [?] in the path
     * @param <T> expected return type
     * @return list of objects matched by the given path
     */
    public static JsonNode read(JsonNode json, String jsonPath, Filter... filters) {
        notNull(json, "json can not be null");
        notNull(jsonPath, "jsonPath can not be null");
        return compile(jsonPath, filters).read(json);
    }

    /**
     * Creates a new JsonPath and applies it to the provided Json object
     * 
     * @param jsonURL url pointing to json doc
     * @param jsonPath the json path
     * @param filters filters to be applied to the filter place holders [?] in the path
     * @param <T> expected return type
     * @return list of objects matched by the given path
     */
    public static JsonNode read(URL jsonURL, String jsonPath, Filter... filters) throws IOException {
        notNull(jsonURL, "json URL can not be null");
        notEmpty(jsonPath, "jsonPath can not be null or empty");
        return compile(jsonPath, filters).read(jsonURL);
    }

    /**
     * Creates a new JsonPath and applies it to the provided Json object
     * 
     * @param jsonFile json file
     * @param jsonPath the json path
     * @param filters filters to be applied to the filter place holders [?] in the path
     * @param <T> expected return type
     * @return list of objects matched by the given path
     */
    public static JsonNode read(File jsonFile, String jsonPath, Filter... filters) throws IOException {
        notNull(jsonFile, "json file can not be null");
        notEmpty(jsonPath, "jsonPath can not be null or empty");
        return compile(jsonPath, filters).read(jsonFile);
    }

    /**
     * Creates a new JsonPath and applies it to the provided Json object
     * 
     * @param jsonInputStream json input stream
     * @param jsonPath the json path
     * @param filters filters to be applied to the filter place holders [?] in the path
     * @param <T> expected return type
     * @return list of objects matched by the given path
     */
    public static JsonNode read(InputStream jsonInputStream, String jsonPath, Filter... filters) throws IOException {
        notNull(jsonInputStream, "json input stream can not be null");
        notEmpty(jsonPath, "jsonPath can not be null or empty");
        return compile(jsonPath, filters).read(jsonInputStream);
    }

}
