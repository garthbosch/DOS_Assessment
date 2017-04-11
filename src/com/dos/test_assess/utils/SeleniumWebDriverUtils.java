package com.dos.test_assess.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.cisco.hcs.rtp.test.auto.utils.logging.Logging;
import com.dos.test_assess.enums.PageElements;

/**
 * @author Garth Bosch
 */
public class SeleniumWebDriverUtils {

    public WebDriver driver;

    File fileIEDriver;

    File fileChromeDriver;

    File fileFirefoxDriver;

    private Boolean isDriverRunning;

    public String driverExceptionMessages = "";

    protected String url = "";

    protected int waitTimeout = 0;

    protected String browserType;

    public static Logger log = Logging.getLogger(true); // set the logger

    public SeleniumWebDriverUtils(String selectedBrowserType, String url, int waitTimeout) {
        isDriverRunning = false;
        fileIEDriver = new File("lib\\IEDriverServer.exe");
        System.setProperty("webdriver.ie.driver", fileIEDriver.getAbsolutePath());
        fileChromeDriver = new File("lib\\chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", fileChromeDriver.getAbsolutePath());
        fileFirefoxDriver = new File("lib\\geckodriver.exe");
        System.setProperty("webdriver.gecko.driver", fileFirefoxDriver.getAbsolutePath());
        browserType = selectedBrowserType;
        this.url = url;
        this.waitTimeout = waitTimeout;
    }

    public boolean getIsDriverRunning() {
        return isDriverRunning;
    }

    public void startDriver() throws Exception {
        if (isDriverRunning) {
            shutDown();
        } else {
            if (browserType.equals(BrowserType.CHROME)) {
                closeChromeInstances();
                driver = new ChromeDriver(setCapabilities("Chrome"));
                isDriverRunning = true;
            }
            if (browserType.equalsIgnoreCase(BrowserType.IE) || browserType.equalsIgnoreCase(BrowserType.IEXPLORE) || browserType.equalsIgnoreCase("ie")) {
                closeIEInstances();
                driver = new InternetExplorerDriver(setCapabilities("IE"));
                isDriverRunning = true;
            }
            if (browserType.equals(BrowserType.FIREFOX)) {
                closeFirefoxInstances();
                driver = new FirefoxDriver(setCapabilities("FireFox"));
                isDriverRunning = true;
            }
            if (url != null && !url.isEmpty()) {
                driver.get(url);
                driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
                driver.manage().timeouts().pageLoadTimeout(waitTimeout, TimeUnit.SECONDS);
                 driver.manage().window().maximize();
                 driver.manage().deleteAllCookies();
            } else {
                throw new Exception("====================NO URL SPECIFIED======================");
            }
        }
    }

    private DesiredCapabilities setCapabilities(String browserTypes) {
        DesiredCapabilities dc = null;
        log.info("[Info] Browser type is: " + browserTypes);
        if (browserTypes.equalsIgnoreCase("Chrome")) {
            dc = DesiredCapabilities.chrome();
            dc.setCapability(CapabilityType.ELEMENT_SCROLL_BEHAVIOR, true);
            dc.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, true);
            dc.setJavascriptEnabled(true);
            /* added this code to remove the error message chrome was throwing You are using an unsupported command-line flag:
             * --ignore-certificate-errors. Stability and security will suffer */
            ChromeOptions options = new ChromeOptions();
            options.addArguments("test-type");
            dc.setCapability(ChromeOptions.CAPABILITY, options);
            return dc;
        }
        if (browserTypes.equalsIgnoreCase("IE")) {
            dc = DesiredCapabilities.internetExplorer();
            dc.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            dc.setCapability(CapabilityType.ELEMENT_SCROLL_BEHAVIOR, true);
            dc.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, true);
            dc.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
            dc.setJavascriptEnabled(true);
            return dc;
        }
        if (browserTypes.equalsIgnoreCase("FireFox")) {
            dc = DesiredCapabilities.firefox();
            dc.setCapability(CapabilityType.ELEMENT_SCROLL_BEHAVIOR, true);
            dc.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, true);
            dc.setCapability("marionette", true);
            dc.setJavascriptEnabled(true);
            return dc;
        }
        log.info("[Info] The browser name is: " + dc.getCapability("Chrome"));
        return dc;
    }

    public void pause(int milisecondsToWait) {
        try {
            Thread.sleep(milisecondsToWait);
            log.info("[Info] Successfully waited for " + milisecondsToWait + " miliseconds");
        } catch (Exception ex) {
            this.driverExceptionMessages = ex.getMessage();
        }
    }

    public void shutDown() {
        try {
            driver.quit();
            closeChromeInstances();
            closeIEInstances();
            closeFirefoxInstances();
        } catch (Exception ex) {
            log.error("[Error] shutting down driver - " + ex.getMessage());
            this.driverExceptionMessages = ex.getMessage();
        }
        isDriverRunning = false;
    }

    public void getBackToButton() throws Exception {
        pause(1000);
        driver.navigate().back();
        pause(1000);
    }

    public String generateDateTimeString() {
        Date dateNow = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        return dateFormat.format(dateNow).toString();
    }

    public void closeChromeInstances() throws IOException {
        if (browserType.equals(BrowserType.CHROME)) {
            String TASKLIST = "tasklist";
            String KILL = "taskkill /IM ";
            String line;
            String chromeServiceName = "chrome.exe";
            String chromeDriverServiceName = "chromedriver.exe*32";
            Process p = Runtime.getRuntime().exec(TASKLIST);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            log.info("Closing Chrome tasks...");
            while ((line = reader.readLine()) != null) {
                if (line.contains(chromeServiceName)) {
                    Runtime.getRuntime().exec(KILL + chromeServiceName);
                }
                if (line.contains(chromeDriverServiceName)) {
                    Runtime.getRuntime().exec(KILL + chromeDriverServiceName);
                }
            }
        }
    }

    public void closeIEInstances() throws IOException {
        if (browserType.equals(BrowserType.IE)) {
            String TASKLIST = "tasklist";
            String KILL = "taskkill /IM ";
            String line;
            String ieServiceName = "iexplore.exe";
            String ieDriverServiceName = "IEDriverServer.exe*32";
            Process p = Runtime.getRuntime().exec(TASKLIST);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            log.info("Closing Chrome tasks...");
            while ((line = reader.readLine()) != null) {
                if (line.contains(ieServiceName)) {
                    Runtime.getRuntime().exec(KILL + ieServiceName);
                }
                if (line.contains(ieDriverServiceName)) {
                    Runtime.getRuntime().exec(KILL + ieDriverServiceName);
                }
            }
        }
    }

    public void closeFirefoxInstances() throws IOException {
        if (browserType.equals(BrowserType.FIREFOX)) {
            String TASKLIST = "tasklist";
            String KILL = "taskkill /IM ";
            String line;
            String firefoxServiceName = "firefox.exe";
            Process p = Runtime.getRuntime().exec(TASKLIST);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            log.info("Closing Chrome tasks...");
            while ((line = reader.readLine()) != null) {
                if (line.contains(firefoxServiceName)) {
                    Runtime.getRuntime().exec(KILL + firefoxServiceName);
                }
            }
        }
    }

    /* Returns true of false whether or not the provided xpath is accessible on the page
     * @param xpath
     * @return Boolean value */
    public boolean waitForXpathAccessibility(final String xpath) throws Exception {
        try {
            WebElement element = (new WebDriverWait(driver,
                    waitTimeout)).until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            // log.info("[Info] Element " + element.toString() + " is accessible.");
            return true;
        } catch (Exception ex) {
            this.driverExceptionMessages = ex.getMessage();
            log.error("[Error] The element " + xpath.toString() + " is not accessible - " + ex.getMessage());
            return false;
        }
    }

    public List<WebElement> findElementsByTag(String tag) throws Exception {
        try {
            log.info("[Info] Element " + tag + " found.");
            return driver.findElements(By.tagName(tag));
        } catch (Exception ex) {
            this.driverExceptionMessages = ex.getMessage();
            log.error("[Error] Element " + tag + " not found - " + ex.getMessage());
            return null;
        }
    }

    public WebElement findElementByXpath(PageElements elements) throws Exception {
        try {
            waitForXpathAccessibility(elements.getElementId());
            log.info("[Info] Element " + elements.getElementNameOnPage() + " found.");
            return driver.findElement(By.xpath(elements.getElementId()));
        } catch (Exception ex) {
            this.driverExceptionMessages = ex.getMessage();
            log.error("[Error] Element " + elements.getElementNameOnPage() + " not found - " + ex.getMessage());
            return null;
        }
    }

    public WebElement findElementByXpathWithoutWait(PageElements elements) throws Exception {
        try {
            waitForXpathAccessibility(elements.getElementId());
            log.info("[Info] Element " + elements.getElementNameOnPage() + " found.");
            return driver.findElement(By.xpath(elements.getElementId()));
        } catch (Exception ex) {
            this.driverExceptionMessages = ex.getMessage();
            log.error("[Error] Element " + elements.getElementNameOnPage() + " not found - " + ex.getMessage());
            return null;
        }
    }

    public List<WebElement> findElementsByXpathWithoutWait(PageElements elements) throws Exception {
        try {
            waitForXpathAccessibility(elements.getElementId());
            log.info("[Info] Element " + elements.getElementNameOnPage() + " found.");
            return driver.findElements(By.xpath(elements.getElementId()));
        } catch (Exception ex) {
            this.driverExceptionMessages = ex.getMessage();
            log.error("[Error] Element " + elements.getElementNameOnPage() + " not found - " + ex.getMessage());
            return null;
        }
    }
}
