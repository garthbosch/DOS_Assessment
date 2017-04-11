package com.dos.test_assess.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

/**
 * @author Garth Bosch
 * @date 11 April 2017
 */
public class AbstractBaseClass {

    protected String filename = null;

    protected static String evidenceFolder = null;

    protected String selectedBrowserType;

    protected String url;

    protected int waitTimeout;

    protected SeleniumWebDriverUtils seleniumDriver;

    public AbstractBaseClass() {
        System.setProperty("java.awt.headless", "true");
    }

    public void captureScreenshot(boolean isError, String selectedBrowserType, String filename) throws Exception {
        String screenshotFile = "";
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(getEvidenceFolder(filename)).append("\\");
            if (isError) {
                sb.append("PASSED_");
            } else {
                sb.append("FAILED_");
            }
            sb.append(seleniumDriver.generateDateTimeString()).append("_").append(filename).append("_").append(selectedBrowserType.toUpperCase()).append(".png");
            screenshotFile = sb.toString();
            File screenCapture = ((TakesScreenshot) seleniumDriver.driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenCapture, new File(screenshotFile));
            System.out.println("[Info] Screenshot " + screenshotFile.toString() + " taken successfully.");
        } catch (IOException ex) {
            Logger.getLogger(AbstractBaseClass.class.getName()).log(Level.FATAL, null, ex);
            System.err.println("[Error] could not take screenshot - " + screenshotFile.toString() + " - " + ex.getMessage());
        }
    }

    public String getEvidenceFolder(String filename) throws IOException {
        String getFolderPath = new File("logs\\" + filename).getPath();
        if (getFolderPath == null) {
            getFolderPath = Runtime.getRuntime().exec("mkdirs logs\\" + filename).toString();
            return getFolderPath;
        }
        return getFolderPath;
    }
}