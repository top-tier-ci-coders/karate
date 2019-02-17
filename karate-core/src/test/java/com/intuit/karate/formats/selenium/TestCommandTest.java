package com.intuit.karate.formats.selenium;

import java.io.File;
import static org.junit.Assert.*;
import org.junit.Test;
import java.lang.*;

import com.intuit.karate.FileUtils;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import static com.intuit.karate.ScriptValue.Type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.util.*;

/**
 * Tests for the TestCommand class
 * @author Andreas Gylling
 */
public class TestCommandTest {

    /**
    * Tests some uncovered tests of the convert Class
    * @author Andreas Gylling
    */
    @Test
    public void convertTest() {
        HashMap<String, String> hashedMap = new HashMap<String, String>();
        hashedMap.put("target", "//=//");
        hashedMap.put("value", "");

        String url = "https://github.com/SeleniumHQ/selenium-ide/blob/master/packages/selianize/src/command.js";
        Map<String, Object> myMap = new HashMap<String, Object>();
        myMap.put("id", "96df602e-8d02-4524-8e57-1784c9451e9c");
        myMap.put("comment", "Nice work");
        myMap.put("command", "click");
        myMap.put("target", "//=//");
        myMap.put("value", "");
        TestCommand tc = new TestCommand(myMap);

        String res = tc.convert(url, hashedMap);
        // Strings that should be contained within the click command
        assertTrue(res.contains("xpath"));
        assertTrue(res.contains("And request {}\n"));
        assertTrue(res.contains("click"));
        assertFalse(res.contains("clickAt"));
        assertFalse(res.contains("clickAndWait"));
        // Test the other conditions wish returns with the same function
        myMap.put("command", "clickAt");
        tc = new TestCommand(myMap);
        res = tc.convert(url,hashedMap);
        assertTrue(res.contains("clickAt"));
        assertFalse(res.contains("clickAndWait"));
        // clickAndWait command
        myMap.put("command", "clickAndWait");
        tc = new TestCommand(myMap);
        res = tc.convert(url,hashedMap);
        assertTrue(res.contains("clickAndWait"));
        assertFalse(res.contains("clickAt"));

        myMap.put("command", "verifyText");
        tc = new TestCommand(myMap);
        res = tc.convert(url,hashedMap);
        assertTrue(res.contains("/text"));
        assertFalse(res.contains("click"));
        // Should be same
        myMap.put("command", "assertText");
        tc = new TestCommand(myMap);
        res = tc.convert(url,hashedMap);
        assertTrue(res.contains("/text"));
        assertFalse(res.contains("click"));

        myMap.put("command", "verifyTitle");
        tc = new TestCommand(myMap);
        res = tc.convert(url,hashedMap);
        assertTrue(res.contains("title"));
        assertFalse(res.contains("/text"));
        assertFalse(res.contains("click"));

        myMap.put("command", "assertTitle");
        tc = new TestCommand(myMap);
        res = tc.convert(url,hashedMap);
        assertTrue(res.contains("title"));
        assertFalse(res.contains("/text"));
        assertFalse(res.contains("click"));

        myMap.put("command", "echo");
        tc = new TestCommand(myMap);
        res = tc.convert(url,hashedMap);
        assertTrue(res.contains("echo"));
        assertTrue(res.contains("* print //=//\n"));
        assertFalse(res.contains("title"));
        assertFalse(res.contains("/text"));
        assertFalse(res.contains("click"));

        myMap.put("command", "pause");
        tc = new TestCommand(myMap);
        res = tc.convert(url,hashedMap);
        assertTrue(res.contains("* def sleep = function(pause){ java.lang.Thread.sleep(pause) }\n"));
        assertTrue(res.contains("pause"));
        assertFalse(res.contains("echo"));
        assertFalse(res.contains("* print //=//\n"));
        assertFalse(res.contains("title"));
        assertFalse(res.contains("/text"));
        assertFalse(res.contains("click"));

    }

}
