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

import com.intuit.karate.http.HttpClient;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import com.intuit.karate.core.AdhocCoverageTool;

/**
 *
 * @author pthomas3
 */
public class Config {

    public static final int DEFAULT_RETRY_INTERVAL = 3000;
    public static final int DEFAULT_RETRY_COUNT = 3;    

    private boolean sslEnabled = false;
    private String sslAlgorithm = "TLS";
    private String sslKeyStore;
    private String sslKeyStorePassword;
    private String sslKeyStoreType;
    private String sslTrustStore;
    private String sslTrustStorePassword;
    private String sslTrustStoreType;
    private boolean sslTrustAll = true;
    private boolean followRedirects = true;
    private int readTimeout = 30000;
    private int connectTimeout = 30000;
    private Charset charset = FileUtils.UTF8;
    private String proxyUri;
    private String proxyUsername;
    private String proxyPassword;
    private List<String> nonProxyHosts;
    private ScriptValue headers = ScriptValue.NULL;
    private ScriptValue cookies = ScriptValue.NULL;
    private ScriptValue responseHeaders = ScriptValue.NULL;
    private boolean lowerCaseResponseHeaders = false;
    private boolean corsEnabled = false;
    private boolean logPrettyRequest;
    private boolean logPrettyResponse;
    private boolean printEnabled = true;
    private String clientClass;
    private HttpClient clientInstance;
    private Map<String, Object> userDefined;
    private Map<String, Object> driverOptions;
    private ScriptValue afterScenario = ScriptValue.NULL;
    private ScriptValue afterFeature = ScriptValue.NULL;

    // retry config
    private int retryInterval = DEFAULT_RETRY_INTERVAL;
    private int retryCount = DEFAULT_RETRY_COUNT;

    // report config
    private boolean showLog = true;
    private boolean showAllSteps = true;

    public Config() {
        // zero arg constructor
    }
    
    private static <T> T get(Map<String, Object> map, String key, T defaultValue) {
        Object o = map.get(key);
        return o == null ? defaultValue : (T) o;
    }

    public boolean configure(String key, ScriptValue value) { // TODO use enum
        AdhocCoverageTool.m.get("configure")[0] = true;
        key = StringUtils.trimToEmpty(key);
        switch (key) {
            case "headers":
                AdhocCoverageTool.m.get("configure")[1] = true;
                headers = value;
                return false;
            case "cookies":
                AdhocCoverageTool.m.get("configure")[2] = true;
                cookies = value;
                return false;
            case "responseHeaders":
                AdhocCoverageTool.m.get("configure")[3] = true;
                responseHeaders = value;
                return false;
            case "lowerCaseResponseHeaders":
                AdhocCoverageTool.m.get("configure")[4] = true;
                lowerCaseResponseHeaders = value.isBooleanTrue();
                return false;
            case "cors":
                AdhocCoverageTool.m.get("configure")[5] = true;
                corsEnabled = value.isBooleanTrue();
                return false;
            case "logPrettyResponse":
                AdhocCoverageTool.m.get("configure")[6] = true;
                logPrettyResponse = value.isBooleanTrue();
                return false;
            case "logPrettyRequest":
                AdhocCoverageTool.m.get("configure")[7] = true;
                logPrettyRequest = value.isBooleanTrue();
                return false;
            case "printEnabled":
                AdhocCoverageTool.m.get("configure")[8] = true;
                printEnabled = value.isBooleanTrue();
                return false;
            case "afterScenario":
                AdhocCoverageTool.m.get("configure")[9] = true;
                afterScenario = value;
                return false;
            case "afterFeature":
                AdhocCoverageTool.m.get("configure")[10] = true;
                afterFeature = value;
                return false;
            case "report":
                AdhocCoverageTool.m.get("configure")[11] = true;
                if (value.isMapLike()) {
                    AdhocCoverageTool.m.get("configure")[12] = true;    
                    Map<String, Object> map = value.getAsMap();
                    showLog = get(map, "showLog", showLog);
                    showAllSteps = get(map, "showAllSteps", showAllSteps);
                } else if (value.isBooleanTrue()) {
                    AdhocCoverageTool.m.get("configure")[13] = true;    
                    showLog = true;
                    showAllSteps = true;
                } else {
                    AdhocCoverageTool.m.get("configure")[14] = true;    
                    showLog = false;
                    showAllSteps = false;
                }
                return false;
            case "driver":
                AdhocCoverageTool.m.get("configure")[15] = true;
                driverOptions = value.getAsMap();
                return false;
            case "retry":
                AdhocCoverageTool.m.get("configure")[16] = true;
                if (value.isMapLike()) {
                    AdhocCoverageTool.m.get("configure")[17] = true;    
                    Map<String, Object> map = value.getAsMap();
                    retryInterval = get(map, "interval", retryInterval);
                    retryCount = get(map, "count", retryCount);                    
                }
                return false;
            // here on the http client has to be re-constructed ================
            case "httpClientClass":
                AdhocCoverageTool.m.get("configure")[18] = true;
                clientClass = value.getAsString();
                return true;
            case "httpClientInstance":
                AdhocCoverageTool.m.get("configure")[19] = true;
                clientInstance = value.getValue(HttpClient.class);
                return true;
            case "charset":
                AdhocCoverageTool.m.get("configure")[20] = true;
                charset = value.isNull() ? null : Charset.forName(value.getAsString());
                return true;
            case "ssl":
                AdhocCoverageTool.m.get("configure")[21] = true;
                if (value.isString()) {
                    AdhocCoverageTool.m.get("configure")[22] = true;
                    sslEnabled = true;
                    sslAlgorithm = value.getAsString();
                } else if (value.isMapLike()) {
                    AdhocCoverageTool.m.get("configure")[23] = true;
                    sslEnabled = true;
                    Map<String, Object> map = value.getAsMap();
                    sslKeyStore = (String) map.get("keyStore");
                    sslKeyStorePassword = (String) map.get("keyStorePassword");
                    sslKeyStoreType = (String) map.get("keyStoreType");
                    sslTrustStore = (String) map.get("trustStore");
                    sslTrustStorePassword = (String) map.get("trustStorePassword");
                    sslTrustStoreType = (String) map.get("trustStoreType");
                    String trustAll = (String) map.get("trustAll");
                    if (trustAll != null) {
                        AdhocCoverageTool.m.get("configure")[24] = true;
                        sslTrustAll = Boolean.valueOf(trustAll);
                    }
                    sslAlgorithm = (String) map.get("algorithm");
                } else {
                    AdhocCoverageTool.m.get("configure")[25] = true;
                    sslEnabled = value.isBooleanTrue();
                }
                return true;
            case "followRedirects":
                AdhocCoverageTool.m.get("configure")[26] = true;
                followRedirects = value.isBooleanTrue();
                return true;
            case "connectTimeout":
                AdhocCoverageTool.m.get("configure")[27] = true;
                connectTimeout = value.getAsInt();
                return true;
            case "readTimeout":
                AdhocCoverageTool.m.get("configure")[28] = true;
                readTimeout = value.getAsInt();
                return true;
            case "proxy":
                AdhocCoverageTool.m.get("configure")[29] = true;
                if (value.isString()) {
                    AdhocCoverageTool.m.get("configure")[30] = true;
                    proxyUri = value.getAsString();
                } else {
                    AdhocCoverageTool.m.get("configure")[31] = true;
                    Map<String, Object> map = value.getAsMap();
                    proxyUri = (String) map.get("uri");
                    proxyUsername = (String) map.get("username");
                    proxyPassword = (String) map.get("password");
                    ScriptObjectMirror temp = (ScriptObjectMirror) map.get("nonProxyHosts");
                    if (temp != null) {
                        AdhocCoverageTool.m.get("configure")[32] = true;
                        nonProxyHosts = (List) temp.values();
                    }
                }
                return true;
            case "userDefined":
                AdhocCoverageTool.m.get("configure")[33] = true;
                userDefined = value.getAsMap();
                return true;
            default:
                AdhocCoverageTool.m.get("configure")[34] = true;
                throw new RuntimeException("unexpected 'configure' key: '" + key + "'");
        }
    }

    public Config(Config parent) {
        sslEnabled = parent.sslEnabled;
        sslAlgorithm = parent.sslAlgorithm;
        sslTrustStore = parent.sslTrustStore;
        sslTrustStorePassword = parent.sslTrustStorePassword;
        sslTrustStoreType = parent.sslTrustStoreType;
        sslKeyStore = parent.sslKeyStore;
        sslKeyStorePassword = parent.sslKeyStorePassword;
        sslKeyStoreType = parent.sslKeyStoreType;
        sslTrustAll = parent.sslTrustAll;
        followRedirects = parent.followRedirects;
        readTimeout = parent.readTimeout;
        connectTimeout = parent.connectTimeout;
        charset = parent.charset;
        proxyUri = parent.proxyUri;
        proxyUsername = parent.proxyUsername;
        proxyPassword = parent.proxyPassword;
        nonProxyHosts = parent.nonProxyHosts;
        headers = parent.headers;
        cookies = parent.cookies;
        responseHeaders = parent.responseHeaders;
        lowerCaseResponseHeaders = parent.lowerCaseResponseHeaders;
        corsEnabled = parent.corsEnabled;
        logPrettyRequest = parent.logPrettyRequest;
        logPrettyResponse = parent.logPrettyResponse;
        printEnabled = parent.printEnabled;
        clientClass = parent.clientClass;
        clientInstance = parent.clientInstance;
        userDefined = parent.userDefined;
        driverOptions = parent.driverOptions;
        afterScenario = parent.afterScenario;
        afterFeature = parent.afterFeature;
        showLog = parent.showLog;
        showAllSteps = parent.showAllSteps;
        retryInterval = parent.retryInterval;
        retryCount = parent.retryCount;
    }
        
    public void setCookies(ScriptValue cookies) {
        this.cookies = cookies;
    }   
    
    public void setClientClass(String clientClass) {
        this.clientClass = clientClass;
    }    

    //==========================================================================
    //
    public boolean isSslEnabled() {
        return sslEnabled;
    }

    public String getSslAlgorithm() {
        return sslAlgorithm;
    }

    public String getSslKeyStore() {
        return sslKeyStore;
    }

    public String getSslKeyStorePassword() {
        return sslKeyStorePassword;
    }

    public String getSslKeyStoreType() {
        return sslKeyStoreType;
    }

    public String getSslTrustStore() {
        return sslTrustStore;
    }

    public String getSslTrustStorePassword() {
        return sslTrustStorePassword;
    }

    public String getSslTrustStoreType() {
        return sslTrustStoreType;
    }

    public boolean isSslTrustAll() {
        return sslTrustAll;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public Charset getCharset() {
        return charset;
    }

    public String getProxyUri() {
        return proxyUri;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public List<String> getNonProxyHosts() {
        return nonProxyHosts;
    }
    
    public ScriptValue getHeaders() {
        return headers;
    }

    public ScriptValue getCookies() {
        return cookies;
    }

    public ScriptValue getResponseHeaders() {
        return responseHeaders;
    }

    public boolean isLowerCaseResponseHeaders() {
        return lowerCaseResponseHeaders;
    }

    public boolean isCorsEnabled() {
        return corsEnabled;
    }

    public boolean isLogPrettyRequest() {
        return logPrettyRequest;
    }

    public boolean isLogPrettyResponse() {
        return logPrettyResponse;
    }

    public boolean isPrintEnabled() {
        return printEnabled;
    }

    public String getClientClass() {
        return clientClass;
    }

    public Map<String, Object> getUserDefined() {
        return userDefined;
    }

    public Map<String, Object> getDriverOptions() {
        return driverOptions;
    }

    public HttpClient getClientInstance() {
        return clientInstance;
    }

    public void setClientInstance(HttpClient clientInstance) {
        this.clientInstance = clientInstance;
    }

    public ScriptValue getAfterScenario() {
        return afterScenario;
    }

    public void setAfterScenario(ScriptValue afterScenario) {
        this.afterScenario = afterScenario;
    }

    public ScriptValue getAfterFeature() {
        return afterFeature;
    }

    public void setAfterFeature(ScriptValue afterFeature) {
        this.afterFeature = afterFeature;
    }

    public boolean isShowLog() {
        return showLog;
    }

    public void setShowLog(boolean showLog) {
        this.showLog = showLog;
    }

    public boolean isShowAllSteps() {
        return showAllSteps;
    }

    public void setShowAllSteps(boolean showAllSteps) {
        this.showAllSteps = showAllSteps;
    }

    public int getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(int retryInterval) {
        this.retryInterval = retryInterval;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

}
