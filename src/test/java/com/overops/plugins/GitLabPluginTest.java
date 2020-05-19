package com.overops.plugins;

import com.overops.report.service.ReportService;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GitLabPluginTest {

    private File qualityReport = new File("/tmp/quality-report.html");

    @Test
    public void testTestMissingHostName() {
        String[] args = new String[]{"--markUnstable=true"};
        GitLabPlugin gitLabPlugin = new GitLabPlugin(new ReportService());
        JobStatus jobStatus = gitLabPlugin.run(args);
        assertEquals(jobStatus, JobStatus.FAIL);
        assertTextExists("OverOps was unable to generate a Quality Report.");
        assertTextExists("Missing host name");
    }

    @Test
    public void testBadParameter() {
        String[] args = new String[]{
                "--markUnstable=true",
                "--maxErrorVolume=asdf"};
        GitLabPlugin gitLabPlugin = new GitLabPlugin(new ReportService());
        JobStatus jobStatus = gitLabPlugin.run(args);
        assertEquals(JobStatus.FAIL, jobStatus);
        assertEquals(1, jobStatus.statusCode());
        assertTextExists("OverOps was unable to generate a Quality Report.");
        assertTextExists("For input string:");
    }

    @Test
    public void testPass() {
        String[] args = new String[]{
                "--overOpsURL=http://api.overops.com",
                "--markUnstable=true",
                "--errorSuccess=true"};

        GitLabPlugin gitLabPlugin = new GitLabPlugin(new ReportService());
        JobStatus jobStatus = gitLabPlugin.run(args);
        assertEquals(jobStatus, JobStatus.PASS);
        assertEquals(0, jobStatus.statusCode());
        assertTextExists("OverOps was unable to generate a Quality Report.");
        assertTextExists("Missing api key");
    }

    @Test
    public void testPassLegitReport() {
        String[] args = new String[]{
                "--overOpsURL=http://api.overops.com",
                "--markUnstable=false",
                "--errorSuccess=true"};

        GitLabPlugin gitLabPlugin = new GitLabPlugin(new ReportService());
        JobStatus jobStatus = gitLabPlugin.run(args);
        assertEquals(jobStatus, JobStatus.PASS);
        assertTextExists("OverOps was unable to generate a Quality Report.");
        assertTextExists("Missing api key");
    }

    private void assertTextExists(String containsString) {
        try {
            String actual = IOUtils.toString(new FileInputStream(qualityReport), Charset.defaultCharset());
            assertTrue(actual.contains(containsString));
        } catch (IOException e) {
            throw new RuntimeException("Unable to assert text exists", e);
        }
    }

    /**
     * Debugging method.  Add to test to open up report in browser.
     */
    private void display() {
        try {
            Desktop.getDesktop().browse(qualityReport.toURI());
        } catch (IOException e) {
            // Do Nothing
        }
    }

}
