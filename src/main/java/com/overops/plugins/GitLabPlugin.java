/**
 * MIT License
 * 
 * Copyright (c) 2020 OverOps, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
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
import com.overops.report.service.ReportGeneratorException;
import com.overops.report.service.ReportService;
import com.overops.report.service.model.QualityReport;
import com.overops.report.service.model.QualityReportExceptionDetails;
import com.overops.report.service.model.ReportStatus;
import com.overops.report.service.model.Requestor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Git Lab Plugin
 */
public class GitLabPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(GitLabPlugin.class);

    private final Config config;

    public GitLabPlugin(Config config) {
        this.config = config;
    }

    /**
     * Logic that runs the quality report with the configuration
     *
     * @return status code
     */
    public int run() {

        int status = 1;
        QualityReport report;
        try {
            String url = config.getOverOpsURL();
            String apiKey = config.getOverOpsAPIKey();
            QualityReportParams params = config.getReportParams();
            Requestor requestor = Requestor.GIT_LAB;
            report = new ReportService().runQualityReport(url, apiKey, params, requestor);
            status = report.getStatusCode() == ReportStatus.FAILED ? 1 : 0;
        } catch (Exception e) {
            // TODO (ccaspanello) Revisit and make sure we have all the scenarios covered.
            QualityReportExceptionDetails details = new QualityReportExceptionDetails();
            details.setExceptionMessage(e.getMessage());

            report = new QualityReport();
            report.setExceptionDetails(details);
            status = config.getReportParams().isMarkUnstable() ? 1 : 0;
        }

        writeQualityReport(report);
        return status;
    }

    /**
     * Writes the quality report to the proper location.
     *
     * @param report
     */
    private void writeQualityReport(QualityReport report) {
        try {
            File writeDirectory = writeDirectory();
            File file = new File(writeDirectory, "quality-report.html");
            LOG.info("Report written to: {}", file.getAbsolutePath());
            IOUtils.write(report.toHtml(), new FileOutputStream(file), Charset.defaultCharset());
        } catch (IOException e) {
            throw new ReportGeneratorException("Unable to generate report.", e);
        }
    }

    /**
     * If the CI_PROJECT_DIR environment variable is defined we will write the quality report to that directory; if not
     * we will write to the /tmp directory.  You will not be able to fetch reports in GitLab from the /tmp directory;
     * this is used primarily for development / debugging.
     *
     * @return folder where we should write the quality report HTML file.
     */
    private File writeDirectory() {
        String ciProjectDir = System.getenv("CI_PROJECT_DIR");
        if (ciProjectDir != null) {
            return new File(ciProjectDir);
        } else {
            LOG.warn("Environment Variable: CI_PROJECT_NAME could not be found; writing to `/tmp` folder.");
            return new File("/tmp");
        }
    }

}
