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
import com.overops.report.service.ReportGeneratorException;
import com.overops.report.service.ReportService;
import com.overops.report.service.model.QualityReport;
import com.overops.report.service.model.QualityReportExceptionDetails;
import com.overops.report.service.model.QualityReport.ReportStatus;
import com.overops.report.service.ReportService.Requestor;
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

    private final ReportService reportService;

    public GitLabPlugin(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Logic that runs the quality report with the configuration
     *
     * @return status code
     */
    public JobStatus run(String[] args) {

        Config config = null;

        try {
            config = new Config(args);
            String url = config.getOverOpsURL();
            String apiKey = config.getOverOpsAPIKey();
            QualityReportParams params = config.getReportParams();

            Requestor requestor = Requestor.GIT_LAB;
            QualityReport report = reportService.runQualityReport(url, apiKey, params, requestor);

            writeHtmlReport(report.toHtml());

            // Determine if we should fail the build
            if (report.getStatusCode() == ReportStatus.FAILED) {
                if ((report.getExceptionDetails() != null) && params.isErrorSuccess()) {
                    // Exceptions found; but we have declared to claim success on errors
                    return JobStatus.PASS;
                } else {
                    // Exceptions found and we have declared to make the job a failure
                    return JobStatus.FAIL;
                }
            } else {
                // Job Successful
                return JobStatus.PASS;
            }

        } catch (Exception e) {
            writeHtmlReport(reportService.exceptionHtml(e));
            return safeFailure(config);
        }
    }

    private JobStatus safeFailure(Config config) {
        try {
            return config.getReportParams().isErrorSuccess() ? JobStatus.PASS : JobStatus.FAIL;
        } catch (NullPointerException npe) {
            return JobStatus.FAIL;
        }
    }

    /**
     * Writes the quality report to the proper location.
     *
     * @param html
     */
    private void writeHtmlReport(String html) {
        try {
            File writeDirectory = determineWriteDirectory();
            File file = new File(writeDirectory, "quality-report.html");
            LOG.info("Report written to: {}", file.getAbsolutePath());
            IOUtils.write(html, new FileOutputStream(file), Charset.defaultCharset());
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
    private File determineWriteDirectory() {
        String ciProjectDir = System.getenv("CI_PROJECT_DIR");
        if (ciProjectDir != null) {
            return new File(ciProjectDir);
        } else {
            LOG.warn("Environment Variable: CI_PROJECT_NAME could not be found; writing to `/tmp` folder.");
            return new File("/tmp");
        }
    }

}
