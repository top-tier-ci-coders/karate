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

import com.intuit.karate.CallContext;
import com.intuit.karate.Script;
import com.intuit.karate.ScriptBindings;
import com.intuit.karate.FileUtils;
import com.intuit.karate.JsonUtils;
import com.intuit.karate.Logger;
import com.intuit.karate.Match;
import com.intuit.karate.ScriptValue;
import com.intuit.karate.ScriptValueMap;
import com.intuit.karate.StepActions;
import com.intuit.karate.StringUtils;
import com.intuit.karate.XmlUtils;
import com.intuit.karate.exception.KarateException;
import com.intuit.karate.http.HttpRequest;
import com.intuit.karate.http.HttpResponse;
import com.intuit.karate.http.HttpUtils;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pthomas3
 */
public class FeatureBackend {

    private final Feature feature;
    private final StepActions actions;

    private boolean corsEnabled;

    private final ScenarioContext context;
    private final String featureName;

    private static void putBinding(String name, ScenarioContext context) {
        String function = "function(s){ return " + ScriptBindings.KARATE + "." + name + "(s) }";
        context.vars.put(name, Script.evalJsExpression(function, context));
    }

    public boolean isCorsEnabled() {
        return corsEnabled;
    }

    public ScenarioContext getContext() {
        return context;
    }

    public FeatureBackend(Feature feature) {
        this(feature, null);
    }

    public FeatureBackend(Feature feature, Map<String, Object> vars) {
        this.feature = feature;
        featureName = feature.getPath().toFile().getName();
        CallContext callContext = new CallContext(null, false);
        FeatureContext featureContext = new FeatureContext(null, feature, null);
        actions = new StepActions(featureContext, callContext, new Logger());
        context = actions.context;
        putBinding(ScriptBindings.PATH_MATCHES, context);
        putBinding(ScriptBindings.METHOD_IS, context);
        putBinding(ScriptBindings.PARAM_VALUE, context);
        putBinding(ScriptBindings.TYPE_CONTAINS, context);
        putBinding(ScriptBindings.ACCEPT_CONTAINS, context);
        putBinding(ScriptBindings.BODY_PATH, context);
        if (vars != null) {
            vars.forEach((k, v) -> context.vars.put(k, v));
        }
        // the background is evaluated one-time
        if (feature.isBackgroundPresent()) {
            for (Step step : feature.getBackground().getSteps()) {
                Result result = Engine.executeStep(step, actions);
                if (result.isFailed()) {
                    String message = "server-side background init failed - " + featureName + ":" + step.getLine();
                    context.logger.error(message);
                    throw new KarateException(message, result.getError());
                }
            }
        }
        // this is a special case, we support the auto-handling of cors
        // only if '* configure cors = true' has been done in the Background
        corsEnabled = context.getConfig().isCorsEnabled(); 
        context.logger.info("backend initialized");
    }

    public ScriptValueMap handle(ScriptValueMap args) {
        boolean matched = false;
        context.vars.putAll(args);
        for (FeatureSection fs : feature.getSections()) {
            if (fs.isOutline()) {
                context.logger.warn("skipping scenario outline - {}:{}", featureName, fs.getScenarioOutline().getLine());
                break;
            }
            Scenario scenario = fs.getScenario();
            if (isMatchingScenario(scenario)) {
                matched = true;
                for (Step step : scenario.getSteps()) {
                    Result result = Engine.executeStep(step, actions);
                    if (result.isAborted()) {
                        context.logger.debug("abort at {}:{}", featureName, step.getLine());
                        break;
                    }
                    if (result.isFailed()) {
                        String message = "server-side scenario failed - " + featureName + ":" + step.getLine();
                        context.logger.error(message);
                        throw new KarateException(message, result.getError());
                    }
                }
                break; // process only first matching scenario
            }
        }
        if (!matched) {
            context.logger.warn("no scenarios matched");
        }
        return context.vars;
    }

    private boolean isMatchingScenario(Scenario scenario) {
        String expression = StringUtils.trimToNull(scenario.getName() + scenario.getDescription());
        if (expression == null) {
            context.logger.debug("scenario matched: (empty)");
            return true;
        }
        try {
            ScriptValue sv = Script.evalJsExpression(expression, context);
            if (sv.isBooleanTrue()) {
                context.logger.debug("scenario matched: {}", expression);
                return true;
            } else {
                context.logger.debug("scenario skipped: {}", expression);
                return false;
            }
        } catch (Exception e) {
            context.logger.warn("scenario match evaluation failed: {}", e.getMessage());
            return false;
        }
    }
    
    private static final String VAR_AFTER_SCENARIO = "afterScenario";
    private static final String ALLOWED_METHODS = "GET, HEAD, POST, PUT, DELETE, PATCH";
    
    public HttpResponse buildResponse(HttpRequest request, long startTime) {
        Boolean[] cov = AdhocCoverageTool.m.get("buildResponse");
        
        if (corsEnabled && "OPTIONS".equals(request.getMethod())) {
            cov[0] = true;
            HttpResponse response = new HttpResponse(startTime, System.currentTimeMillis());
            response.setStatus(200);
            response.addHeader(HttpUtils.HEADER_ALLOW, ALLOWED_METHODS);
            response.addHeader(HttpUtils.HEADER_AC_ALLOW_ORIGIN, "*");
            response.addHeader(HttpUtils.HEADER_AC_ALLOW_METHODS, ALLOWED_METHODS);
            List requestHeaders = request.getHeaders().get(HttpUtils.HEADER_AC_REQUEST_HEADERS);
            if (requestHeaders != null) {
                cov[1] = true;
                response.putHeader(HttpUtils.HEADER_AC_ALLOW_HEADERS, requestHeaders);
            }            
            else
                cov[2] = true;
            return response;
        }
        else
            cov[3] = true;
        Match match = new Match()
                .text(ScriptValueMap.VAR_REQUEST_URL_BASE, request.getUrlBase())
                .text(ScriptValueMap.VAR_REQUEST_URI, request.getUri())
                .text(ScriptValueMap.VAR_REQUEST_METHOD, request.getMethod())
                .def(ScriptValueMap.VAR_REQUEST_HEADERS, request.getHeaders())
                .def(ScriptValueMap.VAR_RESPONSE_STATUS, 200)
                .def(ScriptValueMap.VAR_REQUEST_PARAMS, request.getParams());
        byte[] requestBytes = request.getBody();
        if (requestBytes != null) {
            cov[4] = true;
            match.def(ScriptValueMap.VAR_REQUEST_BYTES, requestBytes);
            String requestString = FileUtils.toString(requestBytes);
            Object requestBody = requestString;
            if (Script.isJson(requestString)) {
                cov[5] = true;
                try {
                    requestBody = JsonUtils.toJsonDoc(requestString);
                } catch (Exception e) {
                    cov[6] = true;
                    context.logger.warn("json parsing failed, request data type set to string: {}", e.getMessage());
                }
            } else if (Script.isXml(requestString)) {
                cov[7] = true;
                try {
                    requestBody = XmlUtils.toXmlDoc(requestString);
                } catch (Exception e) {
                    cov[8] = true;
                    context.logger.warn("xml parsing failed, request data type set to string: {}", e.getMessage());
                }
            }
            else
                cov[9] = true;
            match.def(ScriptValueMap.VAR_REQUEST, requestBody);
        }
        else
            cov[10] = true;
        ScriptValue responseValue, responseStatusValue, responseHeaders, afterScenario;
        Map<String, Object> responseHeadersMap, configResponseHeadersMap;
        // this is a sledgehammer approach to concurrency !
        // which is why for simulating 'delay', users should use the VAR_AFTER_SCENARIO (see end)
        synchronized (this) { // BEGIN TRANSACTION !
            ScriptValueMap result = handle(match.vars());
            ScriptValue configResponseHeaders = context.getConfig().getResponseHeaders();
            responseValue = result.remove(ScriptValueMap.VAR_RESPONSE);
            responseStatusValue = result.remove(ScriptValueMap.VAR_RESPONSE_STATUS);
            responseHeaders = result.remove(ScriptValueMap.VAR_RESPONSE_HEADERS);
            afterScenario = result.remove(VAR_AFTER_SCENARIO);
            if (afterScenario == null) {
                cov[11] = true;
                afterScenario = context.getConfig().getAfterScenario();
            }
            else
                cov[12] = true;
            configResponseHeadersMap = configResponseHeaders == null ? null : configResponseHeaders.evalAsMap(context);
            responseHeadersMap = responseHeaders == null ? null : responseHeaders.evalAsMap(context);
        } // END TRANSACTION !!
        int responseStatus = responseStatusValue == null ? 200 : Integer.valueOf(responseStatusValue.getAsString());
        HttpResponse response = new HttpResponse(startTime, System.currentTimeMillis());
        response.setStatus(responseStatus);
        if (responseValue != null && !responseValue.isNull()) {
            cov[13] = true;
            if (responseValue.isByteArray()) {
                cov[14] = true;
                response.setBody(responseValue.getValue(byte[].class));
            } else {
                cov[15] = true;
                response.setBody(FileUtils.toBytes(responseValue.getAsString()));
            }
        }
        else
            cov[16] = true;
        // trying to avoid creating a map unless absolutely necessary
        if (responseHeadersMap != null) {
            cov[17] = true;
            if (configResponseHeadersMap != null) {
                cov[18] = true;
                responseHeadersMap.putAll(configResponseHeadersMap);
            }
            else
                cov[19] = true;
        } else if (configResponseHeadersMap != null) {
            cov[20] = true;
            responseHeadersMap = configResponseHeadersMap;
        }
        else
            cov[21] = true;
        if (responseHeadersMap != null) {
            cov[22] = true;
            responseHeadersMap.forEach((k, v) -> {
                if (v instanceof List) { // MultiValueMap returned by proceed / response.headers
                    cov[23] = true;
                    response.putHeader(k, (List) v);
                } else if (v != null) {                    
                    cov[24] = true;
                    response.addHeader(k, v.toString());
                }
                else
                    cov[25] = true;
            }); 
        }
        else
            cov[26] = true;
        if (responseValue != null && (responseHeadersMap == null || !responseHeadersMap.containsKey(HttpUtils.HEADER_CONTENT_TYPE))) {
            cov[27] = true;
            response.addHeader(HttpUtils.HEADER_CONTENT_TYPE, HttpUtils.getContentType(responseValue));
        }        
        else
            cov[28] = true;
        if (corsEnabled) {
            cov[29] = true;
            response.addHeader(HttpUtils.HEADER_AC_ALLOW_ORIGIN, "*");
        }
        else
            cov[30] = true;
        // functions here are outside of the 'transaction' and should not mutate global state !
        // typically this is where users can set up an artificial delay or sleep
        if (afterScenario != null && afterScenario.isFunction()) {
            cov[31] = true;
            afterScenario.invokeFunction(context, null);
        }
        else
            cov[32] = true;
        return response;
    }    

}

