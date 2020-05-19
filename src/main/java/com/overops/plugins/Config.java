/**
 * MIT License
 * <p>
 * Copyright (c) 2020 OverOps, Inc.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.overops.plugins;

import com.overops.report.service.QualityReportParams;
import com.takipi.common.util.Pair;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Config {
    private String overOpsURL;
    private String overOpsAPIKey;
    private QualityReportParams reportParams;

    public Config(String[] args) {
        Map<String, String> argsMap = argsToMap(args);
        QualityReportParams params = new QualityReportParams();

        params.setApplicationName(argsMap.get("applicationName"));
        params.setDeploymentName(argsMap.get("deploymentName"));
        params.setServiceId(argsMap.getOrDefault("serviceId", argsMap.get("overOpsSID")));
        params.setRegexFilter(argsMap.getOrDefault("regexFilter", ""));
        params.setMarkUnstable(Boolean.parseBoolean(argsMap.getOrDefault("markUnstable", "false")));
        params.setPrintTopIssues(Integer.parseInt(argsMap.getOrDefault("printTopIssues", "5")));
        params.setNewEvents(Boolean.parseBoolean(argsMap.getOrDefault("newEvents", "false")));
        params.setResurfacedErrors(Boolean.parseBoolean(argsMap.getOrDefault("resurfacedErrors", "false")));
        params.setMaxErrorVolume(Integer.parseInt(argsMap.getOrDefault("maxErrorVolume", "0")));
        params.setMaxUniqueErrors(Integer.parseInt(argsMap.getOrDefault("maxUniqueErrors", "0")));
        params.setCriticalExceptionTypes(argsMap.getOrDefault("criticalExceptionTypes", ""));
        params.setCriticalRegressionDelta(Double.parseDouble(argsMap.getOrDefault("criticalRegressionDelta", "0")));
        params.setActiveTimespan(argsMap.getOrDefault("activeTimespan", "0"));
        params.setBaselineTimespan(argsMap.getOrDefault("baselineTimespan", "0"));
        params.setMinVolumeThreshold(Integer.parseInt(argsMap.getOrDefault("minVolumeThreshold", "0")));
        params.setMinErrorRateThreshold(Double.parseDouble(argsMap.getOrDefault("minErrorRateThreshold", "0")));
        params.setRegressionDelta(Double.parseDouble(argsMap.getOrDefault("regressionDelta", "0")));
        params.setApplySeasonality(Boolean.parseBoolean(argsMap.getOrDefault("applySeasonality", "false")));
        params.setErrorSuccess(Boolean.parseBoolean(argsMap.getOrDefault("errorSuccess", "false")));
        params.setDebug(Boolean.parseBoolean(argsMap.getOrDefault("debug", "false")));

        reportParams = params;
        overOpsURL = argsMap.get("overOpsURL");
        overOpsAPIKey = argsMap.get("overOpsAPIKey");
    }

    private Map<String, String> argsToMap(String[] args) {
        final String parameterDeclaration = "--";
        return Arrays.stream(args).filter(e -> e.startsWith(parameterDeclaration) && e.contains("="))
                .map(e -> e.substring(parameterDeclaration.length()))
                .map(e -> {
                    int i = e.indexOf("=");
                    return Pair.of(e.substring(0, i), e.substring(++i));
                }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    public String getOverOpsURL() {
        return overOpsURL;
    }

    public String getOverOpsAPIKey() {
        return overOpsAPIKey;
    }

    public QualityReportParams getReportParams() {
        return reportParams;
    }
}
