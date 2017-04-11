package com.dos.test_assess.tests;

import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.cisco.hcs.rtp.test.auto.utils.logging.Logging;
import com.dos.test_assess.enums.PageElements;
import com.dos.test_assess.utils.AbstractBaseClass;
import com.dos.test_assess.utils.SeleniumWebDriverUtils;

/**
 * @author Garth Bosch
 * @date 11 April 2017
 */
public class CollectAndDisplayData extends AbstractBaseClass {

    public static Logger log = Logging.getLogger(true); // set the logger

    @Parameters({ "url",
            "filename",
            "selectedBrowserType",
            "waitTimeout", })
    @Test(groups = { "googleNews" })
    public void googleNews(String url, String filename, String selectedBrowserType, int waitTimeout) throws Exception {
        this.url = url;
        this.filename = filename;
        this.selectedBrowserType = selectedBrowserType;
        this.waitTimeout = waitTimeout;
        seleniumDriver = new SeleniumWebDriverUtils(selectedBrowserType,
                url,
                waitTimeout);
        seleniumDriver.startDriver();
        executeGoogleNewsTest();
        clearDown();
    }

    public void executeGoogleNewsTest() throws Exception {
        try {
            captureScreenshot(true, selectedBrowserType, filename);
            List<WebElement> articleTitles = seleniumDriver.findElementsByXpathWithoutWait(PageElements.ArticleTitleText);
            for (int i = 1; i < articleTitles.size(); i++) {
                log.info("Article " + i + ": " + articleTitles.get(i).getText());
            }
        } catch (Exception ex) {
            log.error("[Error] The test has failed with error message - " + ex.getMessage());
        }
    }

    @Parameters({ "url",
            "filename",
            "selectedBrowserType",
            "waitTimeout", })
    @Test(groups = { "sportsBetway" })
    public void sportsBetway(String url, String filename, String selectedBrowserType, int waitTimeout) throws Exception {
        this.url = url;
        this.filename = filename;
        this.selectedBrowserType = selectedBrowserType;
        this.waitTimeout = waitTimeout;
        seleniumDriver = new SeleniumWebDriverUtils(selectedBrowserType,
                url,
                waitTimeout);
        seleniumDriver.startDriver();
        executeSportsBetwayTest();
        clearDown();
    }

    public void executeSportsBetwayTest() throws Exception {
        try {
            captureScreenshot(true, selectedBrowserType, filename);
            List<WebElement> liveGames = seleniumDriver.findElementsByXpathWithoutWait(PageElements.EventListContent);
            for (int i = 1; i < liveGames.size(); i++) {
                log.info("Game " + i + ": " + liveGames.get(i).getAttribute("collectionitem"));
            }
        } catch (Exception ex) {
            log.error("[Error] The test has failed with error message - " + ex.getMessage());
        }
    }

    public void clearDown() {
        seleniumDriver.shutDown();
    }
}