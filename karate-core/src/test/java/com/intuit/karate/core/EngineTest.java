/*
 * The MIT License
 *
 * Copyright 2018 Intuit Inc.
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
package com.intuit.karate.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// testStepHtml
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;


/**
 *
 * @author pthomas3
 */
public class EngineTest {

    private static final Logger logger = LoggerFactory.getLogger(EngineTest.class);    

    /**
     *  Test a feature with passing a scenario adds passed test to the html file
     *  and another feature without a passing scenario don't.
     *
     *  Increase adhoc coverage from 0/22 to 4/22
     *  Increase JaCoCo coverage from 0% to 35%
     *  @author Marcus Östling
     */
    @Test
    public void testStepHtml() {
        {
            Feature feature = FeatureParser.parse(
                    "classpath:com/intuit/karate/core/test-simple-background.feature");
            FeatureResult result = 
                Engine.executeFeatureSync(null, feature, "not('@ignore')", null);
            String filePath = Engine.getBuildDir() + File.separator + "surefire-reports";
            Engine.saveResultHtml(
                    filePath , result, null);

            boolean fileContainsPassed = false;
            try {
                String fileName = filePath + File.separator + 
                    "/com.intuit.karate.core.test-simple-background.html";
                String content = new Scanner(new File(fileName)).useDelimiter("\\Z").next();
                Pattern p = Pattern.compile("step-cell passed");
                Matcher m = p.matcher(content);
                fileContainsPassed = m.find();
            } catch(IOException e) {
                e.printStackTrace();
            }
            assertTrue(fileContainsPassed);
        }
        {
            Feature feature = FeatureParser.parse(
                    "classpath:com/intuit/karate/core/test-ignore-feature.feature");
            FeatureResult result = 
                Engine.executeFeatureSync(null, feature, "not('@ignore')", null);
            String filePath = Engine.getBuildDir() + File.separator + "surefire-reports";
            Engine.saveResultHtml(
                    filePath , result, null);

            boolean fileContainsPassed = false;
            try {
                String fileName = filePath + File.separator + 
                    "/com.intuit.karate.core.test-ignore-feature.html";
                String content = new Scanner(new File(fileName)).useDelimiter("\\Z").next();
                Pattern p = Pattern.compile("step-cell passed");
                Matcher m = p.matcher(content);
                fileContainsPassed = m.find();
            } catch(IOException e) {
                e.printStackTrace();
            }
            assertFalse(fileContainsPassed);
        }
    }
}
