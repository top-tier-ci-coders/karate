package com.intuit.karate.http;

import com.intuit.karate.Match;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashMap;
import com.intuit.karate.ScriptValue;

/**
 *
 * @author pthomas3
 */
public class HttpRequestBuilderTest {
    
    @Test
    public void testRemoveHeaderIgnoreCase() {
        HttpRequestBuilder request = new HttpRequestBuilder();
        request.setHeader("Content-Length", "100");
        Match.equals(request.getHeaders(), "{ 'Content-Length': ['100'] }");
        request.removeHeaderIgnoreCase("content-length");
        Match.equals(request.getHeaders(), "{}");
    }

    @Test
    public void testCopy() {
        HttpRequestBuilder request = new HttpRequestBuilder();
        //request.setHeader("Content-Length", "100");
        //Match.equals(request.getHeaders(), "{ 'Content-Length': ['100'] }");
        //request.removeHeaderIgnoreCase("content-length");
        //Match.equals(request.getHeaders(), "{}");
        
        // url
        assertNull(request.copy().getUrl());
        request.setUrl("test-url");
        assertTrue(request.copy().getUrl().equals("test-url"));

        // paths
        assertNull(request.copy().getPaths());
        request.addPath("test-path");
        assertEquals(request.copy().getPaths().size(), 1);

        // headers
        assertNull(request.copy().getHeaders());
        request.setHeader("test-header", "100");
        assertEquals(request.copy().getHeaders().size(), 1);
        
        // params
        assertNull(request.copy().getParams());
        request.setParam("test-param", "100");
        assertEquals(request.copy().getParams().size(), 1);
        
        // cookies
        assertNull(request.copy().getCookies());
        HashMap<String, String> c = new HashMap<>();
        c.put("test-cookie", "100");
        request.setCookie(new Cookie(c));
        assertEquals(request.copy().getCookies().size(), 1);

        // formFields
        assertNull(request.copy().getFormFields());
        request.setFormField("test-formfield", "100");
        assertEquals(request.copy().getFormFields().size(), 1);
        
        // multipartItems
        assertNull(request.copy().getMultiPartItems());
        request.addMultiPartItem("test-mpi", new ScriptValue(null, "null"));
        assertEquals(request.copy().getMultiPartItems().size(), 1);
        
        // body
        assertNull(request.copy().getBody());
        ScriptValue nullScriptValue = new ScriptValue(null, "null");
        request.setBody(nullScriptValue);
        assertEquals(request.copy().getBody(), nullScriptValue);
        
    }
    
}
