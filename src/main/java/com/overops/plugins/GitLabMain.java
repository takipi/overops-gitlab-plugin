package com.overops.plugins;

import com.overops.report.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Entry Point for the OverOps GitLab Plugin
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

        ReportService reportService = new ReportService();

        // Create & Run Plugin
        GitLabPlugin plugin = new GitLabPlugin(reportService);
        JobStatus status = plugin.run(args);

        System.exit(status.statusCode());
    }

}
