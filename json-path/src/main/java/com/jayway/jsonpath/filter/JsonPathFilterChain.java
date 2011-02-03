package com.jayway.jsonpath.filter;

import org.json.simple.JSONArray;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * User: kallestenflo
 * Date: 2/2/11
 * Time: 2:00 PM
 */
public class JsonPathFilterChain {

    private static final List<Object> EMPTY_LIST = Collections.unmodifiableList(new JSONArray());

    private List<JsonPathFilterBase> filters;

    public JsonPathFilterChain(List<String> pathFragments) {
        filters = configureFilters(pathFragments);
    }

    private List<JsonPathFilterBase> configureFilters(List<String> pathFragments) {

        List<JsonPathFilterBase> configured = new LinkedList<JsonPathFilterBase>();

        for (String pathFragment : pathFragments) {
            configured.add(JsonPathFilterFactory.createFilter(pathFragment));
        }
        return configured;
    }

    public List<Object> filter(Object root) {

        List<Object> rootList = new JSONArray();
        rootList.add(root);

        List<Object> result = rootList;

        for (JsonPathFilterBase filter : filters) {
            result = filter.apply(result);
        }

        return result;
    }
}