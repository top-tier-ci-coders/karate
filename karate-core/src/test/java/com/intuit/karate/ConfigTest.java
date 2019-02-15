package com.intuit.karate;

import com.intuit.karate.core.FeatureContext;
import com.intuit.karate.core.ScenarioContext;
import java.nio.file.Path;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pthomas3
 */
public class ConfigTest {
    
    @Test
    public void testSettingVariableViaKarateConfig() {
        Path featureDir = FileUtils.getPathContaining(getClass());
        FeatureContext featureContext = FeatureContext.forWorkingDir(featureDir.toFile());
        CallContext callContext = new CallContext(null, true);
        ScenarioContext ctx = new ScenarioContext(featureContext, callContext, null);        
        ScriptValue value = Script.evalJsExpression("someConfig", ctx);
        assertEquals("someValue", value.getValue());
    }

    @Test
    /**
     * Tests setting different configurations and making sure that
     * they are set to the correct values.
     * @author Gustaf Pihl
     */
    public void testConfigure() {
        Config c = new Config();
        {
            //value to set the configuration to (true)
            ScriptValue sv = new ScriptValue(true);
            //sets the configuration lowerCaseResponseHeaders
            c.configure("lowerCaseResponseHeaders", sv);
            //gets the value of the configuration lowerCaseResponseHeaders
            boolean b = c.isLowerCaseResponseHeaders();
            //we set it to true so it should be true
            assertTrue(b);
        }
        {
            ScriptValue sv = new ScriptValue(true);
            c.configure("cors", sv);
            boolean b = c.isCorsEnabled();
            assertTrue(b);
        }
        {
            ScriptValue sv = new ScriptValue(true);
            c.configure("logPrettyResponse", sv);
            boolean b = c.isLogPrettyResponse();
            assertTrue(b);
        }
        {
            ScriptValue sv = new ScriptValue(true);
            c.configure("logPrettyRequest", sv);
            boolean b = c.isLogPrettyRequest();
            assertTrue(b);
        }        
        {
            ScriptValue sv = new ScriptValue(true);
            c.configure("printEnabled", sv);
            boolean b = c.isPrintEnabled();
            assertTrue(b);
        }        
        {
            ScriptValue sv = new ScriptValue(true);
            c.configure("report", sv);
            boolean b1 = c.isShowLog();
            assertTrue(b1);
            boolean b2 = c.isShowAllSteps();
            assertTrue(b2);
        }              
    }    
}
