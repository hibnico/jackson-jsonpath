package com.jayway.jsonassert;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.Matcher;

import com.jayway.jsonassert.impl.JsonAsserterImpl;
import com.jayway.jsonassert.impl.matcher.CollectionMatcher;
import com.jayway.jsonassert.impl.matcher.IsCollectionWithSize;
import com.jayway.jsonassert.impl.matcher.IsEmptyCollection;
import com.jayway.jsonassert.impl.matcher.IsMapContainingKey;
import com.jayway.jsonassert.impl.matcher.IsMapContainingValue;
import com.jayway.jsonpath.InvalidJsonException;

/**
 * User: kalle stenflo Date: 1/24/11 Time: 9:31 PM
 */
public class JsonAssert {

    private static ObjectMapper jsonMapper = new ObjectMapper();

    /**
     * Creates a JSONAsserter
     * 
     * @param json the JSON document to create a JSONAsserter for
     * @return a JSON asserter initialized with the provided document
     * @throws ParseException when the given JSON could not be parsed
     */
    public static JsonAsserter with(String json) {
        try {
            return new JsonAsserterImpl(jsonMapper.readTree(json));
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    /**
     * Creates a JSONAsserter
     * 
     * @param reader the reader of the json document
     * @return a JSON asserter initialized with the provided document
     * @throws ParseException when the given JSON could not be parsed
     */
    public static JsonAsserter with(Reader reader) throws IOException {
        return with(convertReaderToString(reader));
    }

    /**
     * Creates a JSONAsserter
     * 
     * @param is the input stream
     * @return a JSON asserter initialized with the provided document
     * @throws ParseException when the given JSON could not be parsed
     */
    public static JsonAsserter with(InputStream is) throws IOException {
        Reader reader = new InputStreamReader(is);
        return with(reader);
    }

    // Matchers

    public static CollectionMatcher collectionWithSize(Matcher< ? super Integer> sizeMatcher) {
        return new IsCollectionWithSize(sizeMatcher);
    }

    public static Matcher<Map<String, ? >> mapContainingKey(Matcher<String> keyMatcher) {
        return new IsMapContainingKey(keyMatcher);
    }

    public static <V> Matcher< ? super Map< ? , V>> mapContainingValue(Matcher< ? super V> valueMatcher) {
        return new IsMapContainingValue<V>(valueMatcher);
    }

    public static Matcher<Collection<Object>> emptyCollection() {
        return new IsEmptyCollection<Object>();
    }

    private static String convertReaderToString(Reader reader) throws IOException {
        if (reader != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                reader.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

}
