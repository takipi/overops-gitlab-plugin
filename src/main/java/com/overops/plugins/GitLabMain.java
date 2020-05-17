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

/**
 * Main Entry Point for the Overops GitLab Plugin
 */
public class GitLabMain {

    private static final Logger LOG = LoggerFactory.getLogger(GitLabMain.class);

    public static void main(String[] args) {

        LOG.info("*********************************");
        LOG.info("Generating OverOps Quality Report");
        LOG.info("*********************************");
        LOG.debug("Arguments:");
        for (String arg : args) {
            LOG.debug("  - {}", arg);
        }

        // Parse Args
        Config config = new Config(args);

        // Create & Run Plugin
        GitLabPlugin plugin = new GitLabPlugin(config);
        int status = plugin.run();

        System.exit(status);
    }

}
