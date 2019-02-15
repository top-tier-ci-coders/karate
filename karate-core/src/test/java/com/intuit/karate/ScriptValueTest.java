/*
 * The MIT License
 *
 * Copyright 2017 Intuit Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.intuit.karate;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import static com.intuit.karate.ScriptValue.Type.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author pthomas3
 */
public class ScriptValueTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ScriptValueTest.class);
   
    @Test
    public void testTypeDetection() {
        DocumentContext doc = JsonPath.parse("{ foo: 'bar' }");
        ScriptValue sv = new ScriptValue(doc);
        assertEquals(JSON, sv.getType());
        doc = JsonPath.parse("[1, 2]");
        sv = new ScriptValue(doc);
        assertEquals(JSON, sv.getType());  
        Object temp = doc.read("$");
        assertTrue(temp instanceof List);
        sv = new ScriptValue(1);
        assertTrue(sv.isPrimitive());
        assertTrue(sv.isNumber());
        assertEquals(1, sv.getAsNumber().intValue());
        sv = new ScriptValue(100L);
        assertTrue(sv.isPrimitive());
        assertTrue(sv.isNumber());
        assertEquals(100, sv.getAsNumber().longValue());
        sv = new ScriptValue(1.0);
        assertTrue(sv.isPrimitive());
        assertTrue(sv.isNumber());
        assertEquals(1.0, sv.getAsNumber().doubleValue(), 0);         
    }

    /**
     * This test is to add coverage to getAsString. It is currently not testing
     * for some types like MAP, LIST etc.
     * The getAsString method should return the scriptValue values as Strings.
     * These tests increases the coverage from 45% to 64% in JaCoco.
     * @author Philippa Ö
     */
    @Test
    public void testGetAsString(){
        // Want to create object of type MAP
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("foo", "bar"); // {foo = bar}
        testMap.put("bar", "baz"); // {bar = baz}
        ScriptValue svMAP = new ScriptValue(testMap); // [type: MAP, value: {foo=bar}]
        String svAsString = svMAP.getAsString();
        assertTrue(svAsString instanceof String); // Make sure that the object is now of class String

        // Checks that getAsString gives the expected output as shown below
        assertEquals(true,"{\"bar\":\"baz\",\"foo\":\"bar\"}".equals(svAsString)); // Check that the conversion is correct
        Map<String, int[]> testMapList = new HashMap<>();
        testMapList.put("foo", new int[10]);
        for(int i = 0; i < 10; i++){
            testMapList.get("foo")[i] = i;
        }
        ScriptValue mapList = new ScriptValue(testMapList);
        // Checks that getAsString gives the expected output as shown below
        assertEquals(true, "{\"foo\":[0,1,2,3,4,5,6,7,8,9]}".equals(mapList.getAsString())); // Check that it works for map with list too

        List<Integer> testList = new ArrayList<>(); // Want to check that the function works on lists
        testList.add(10);
        testList.add(15);
        ScriptValue svLIST = new ScriptValue(testList);
        assertTrue(svLIST.getAsString() instanceof String); // getAsString gives us a string.
        // Checks that getAsString gives the expected output as shown below
        assertEquals(true, "[10,15]".equals(svLIST.getAsString()));

    }
    
}
