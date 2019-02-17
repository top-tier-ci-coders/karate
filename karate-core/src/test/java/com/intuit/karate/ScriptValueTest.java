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
import java.util.List;
import java.util.*;
import java.lang.*;
import java.io.*;
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
    @author Andreas Gylling
    */
    @Test
    public void testGetTypeAsShortString(){

      //JSON TYPE
      DocumentContext doc = JsonPath.parse("{ foo: 'bar' }");
      ScriptValue sv = new ScriptValue(doc, ".");
      assertEquals("json", sv.getTypeAsShortString());

      sv = new ScriptValue(null, ".");
      assertEquals("null",sv.getTypeAsShortString());
      assertTrue(sv.isNull());

      //XML TYPE (instance of NODE)
      // TODO

      //LIST TYPE
      doc = JsonPath.parse("[1, 2]");
      Object myList = doc.read("$");
      assertTrue(myList instanceof List);
      sv = new ScriptValue(myList, ".");
      assertEquals("list",sv.getTypeAsShortString());

      // Map JS_ARRAY TYPE
      // TODO

      //Map JS_FUNCTION TYPE
      // TODO

      //Map JS_OBJECT TYPE
      // TODO

      // MAP TYPE
      // TODO

      // STRING TYPE
      sv = new ScriptValue("string type", ".");
      assertEquals("str", sv.getTypeAsShortString());
      assertTrue(sv.isString());
      // BYTE_ARRAY TYPE
      byte[] ba = "bytearray".getBytes();
      sv = new ScriptValue(ba, ".");
      assertEquals("byte[]", sv.getTypeAsShortString());
      assertTrue(sv.isByteArray());
      // INPUT STREAM TYPE
      sv = new ScriptValue(System.in, ".");
      assertEquals("stream", sv.getTypeAsShortString());
      assertTrue(sv.isStringOrStream());
      assertTrue(sv.isStream());
      // PRIMITIVE TYPE
      sv = new ScriptValue(1, ".");
      assertEquals("num", sv.getTypeAsShortString());
      assertTrue(sv.isPrimitive());
      assertTrue(sv.isNumber());

      // FEATURE TYPE
      // TODO
      
      // UNKNOWN TYPE
      // TODO
    }
}
