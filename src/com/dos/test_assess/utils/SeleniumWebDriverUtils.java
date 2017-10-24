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
    
    //((JavascriptExecutor) driver).executeScript("document.getElementById('AdvisorSignature').value = '0AEiEY2FnBe9yn/GbChFEkfijdX34lDeupCFYKb8qfpCBU0MefeOeswuEgRnRUbpWGsFLVpCl9uA0x4j0Lt9qbT/1ngTey00m1KcX+hpI+13NDwueC4xRaFioVbRdat9IzXkF03Y5nDuhkm6dQxtOkYafwMucwCpCZcyxOcFVz0zToHKub1cLkc0+/IMQQmJyG1ZZNghkUTs57DCMLBNvidY2elZy1YfCIeMSN2M4BLiCuZDK/IvhTNf6j/zD08QqLk+vkUQ3PR570Mor7Tt/j0/Q+8oLm4Af0DbSrniO7f/oZV3ekIQQNcBT7spfhr50zFvKhBU2UfHpXw6CIJ1n7O2iCMrmT9nE9WlwdPNIk6cHAE2liQP9iBIEPSS4aliBD/bRMkEiRjJ7sjvF2Iid22vH6wgzOq23C1jVp7z/S1q+MOLEoQcd4eQ09V2zV8iJDGC4Kg0PWIOpdFuABnaS/Ukyyl3B9qGxa7CatTNTOgF2/rtu8VM2E2XDWkB5XntNO4Y3LojTAu5+rZIdIWtz8IHY5WnwjT51B+lmacDG5KvFPdeeoR6E25XXCzkaxOkNvHbc8P3RuxsStBAEf9MysdiQTm2OojPSAbldIjxQAbRX//ggttzBrP91FYjMriuC2cF1TrjoaCttIYD7o08+zq4Pw+LubMDcJBRRcYbilRo8QjTh9awdVXkb4yJb+vjqp9TYSKp4Qtl+9BgLGC0cxoG/j4L9nA88psc1EuQAUFSOyCpUhjK0Z3aidLXu/L7nBxpm5ujYdGIaRpiMcd8rQwjepEK7Hf1jWa6UH72IEs0XUfZc2bIkIUU2GR7CqZKMOftl1diXBtRb8wHvqHkmgcik12fNB1sWr39DUx7o3VbEDy7kzcoeLHfJjal4Um8HdGMeNKZWUOIxFmVyIRl16GkGWLj2yn+2hhjMn8ABngWfM/Yv9RgndKYB/KxPPMHsQFHD/0Lb8y2FGaKp8c7iInf64T9Gt/kNXcRVoQrcp1b+NYUNoUq36pfHSLmw1DhHLJSkgn1Bou8VcDX2SWeV8OjSsb9TT0oDYIw1zMZ/DMIkhJbftgnRHLecfyMwY6ibIngIThrxfeAPPLS13GI//fpkRcca7fYjkXm1uyxFtMDWNJfKhqiypmpcUg9+W9b5XK/opHpNhO4pY2oy19Nnk4JRjfTtcZbq39hsDS11Cpv9L256LnKMnCBhz1qra1v829+O4wduNXsLc1usJFV6OPltPNFbEVm/GVLn81/2Wm/Xaumo7Xir31KV2z6BZ+eFelPI0pu5lotuusS+Oido/vXlMXeYp2db6mPhTZ6vGEVTF/xy6Us4EAFfZoHOPhgJaVHtvciQOlpXOgnR7/o3s06xbkvI9QIFj4qdm5fbYc7r5jHAH6c3fspM2vMArbTM39Vm40/kZoUst483BPK4YneRh1X/ivAezyJBHWU88zfXYFAi3l9E5kCk1FQPwk7izgjGAlFRDU/WP/138mNDldStR6iE5S2YeYnGUl5+euSCcrb+8m93Z6x356r3jvgVJvrwtHmUEO31j/5E9gbXGDanXqUnbzxQp5zdacN21UQYIQcnDwMD3yc20CUVTOeYx/jrYZPBZmYxyjoaohzpOsgucT3VghyPJxjgbtjgE5r5tnujXsckq8X3vStWSEMZCM/zBwoJqrEaej6d0rIX5ZaS4beChzb6Dp9cUIIK4aPGbjalW1XuqZzN8J5nvRezRd+2h2aKilAlyGv6optcQz0rYOXZr+vs5cf00P6s++7/34RXJa04MHAZOfRSckI1tSH/U32+peVeGAMgLFMGfsjNYd328jphTx75xC8Z7bZBMggxAgurZL3KaPWnF4VevvmzC52Y2IEYZ4dhXJqmCAY9JPorunQP/57U9D2jrE+7hHJF4acqpqsie6+VkDM7RdWJfd6xYWKH/RM/YQ+wEK77OPX4GnlMi3K1JG9+7OnyK3eDzjQgHF7YMkKXENzdqO3uhm8SAxnuzfWFOWSdgjZi2hg97YMyBS6Pb831NcWZW1JnoY/Y7mCzje85JaeQgIZEn+TSbZkrwftS/iFsGmpC7XrdkCINxztyHGFvh9g9mj8KV0xRCLWX3VOlAiMmn1jx6BxYmScTN2/uQT08lHd2bjzcDcE/nx2qZHG/ZDknJOg3Hg7+oNcuSlaNl4gh1+r9/NJ1em+xLWOJnuP6cfsCsXO678IWSkYGSEGP5PLlS0zb56CPGb6qcz//o0m8yUysQrY3OVtZPQ0fhBl+rhfG+pZ7grrVYoeEx5ZUeWpEfzYfe5jCujD4WVSHtkkQzIQwQSpvVs6bPbNUpa6VWUvbY57riCiO7r7gsWmJsRRYyNQIFFDtrbHRy1UyleEFgWoHu5DQedRHLvWBAOTUxk1RFCZp45TgfEgrh/OjxXZ0T5NIBmJnPL4reyEnjJJrNPZbKL4zL1bAY1H0QvG9/Fq+DhCGLYWE2lsj36EWQpk+CXuSf3BCcicyjgvIRrHWNAEuoxuUsKvVOvEpaVdnLdIFICtfoMfw9nJsG4KqmJphVJnNvvTx/eu6HEITxgXd4E8DGfOmAqoOpdW/hTEPaNEAIcvveDcNyiZR2nzvJ+1vHLL+Pr5AmIjhHiskwiBYREn3oh6K1TbaGl8IQ+exjdJyRjgLhyog4CA46Pfgt3/Qey5Fp/uHa6vwu4ngrCLXgAfDZE2fRgpKMwuWk68YU5ZicID0HmEdZtTRXLjF2UCXo5e9SEkSZcnsZM6xQe5L/aQB4L8scygzi/9k8F/fEfweFII+n3RaA87RSS/uY5bjDn9bCdTk7ONiR2IcpJyMaVTlo8lyJ8WJ40yK3cZemN885QdNbgcvXQi4zzFw2ln3Wh9nzkrksSgwLKFVTD+sC3y6XClWsQufmRcnO+WFwEvAH034LOZ/ZjtmV78Hr7BFfSueEfVZk6UAzb5b6ddABsyANkxxOSf8Rs5qpqxmGZZaLDRbhTvu75wOT7CbT6tznW2vGt5V6PBLGhzETHkvDNJWSRIKmEZDRcoTbARbIz4cZ7VDqEc8Mc7zFq1LyQs43IMYFQcffFpEMJVv1rZ/sAv3kfNLTlsfDiQ5LFVF5sc60V2BR7+UQ7qi6jt+LZ/lDzVMaJVsxa08G1/yAPUlxSGLs1+CbAkHn+orqjoUXokEf+YV5CvnYmF3TX2M3uLpwnWNhiywRXcRDzX0tPqy+gQi0Ox8wywviRrzQqGaq1KhX+5Ug5ta+ErrQ9d3hFfmiKtjTBkJxeI8etwLmygtJTPzFVHpKweRV0KCImZl4MNooG9JnJphS1voYMmMrCSe33+RSjNmZ5m2gVT8qu7CbX4O35eR0uphFZUlaxtcf1lj9r41CR76Iv01Qcd4Z6oFcMzkjEbgRpU9WzO4tlCP4DM/1kaVHU1Mnfxs9BqW4IZClLddJZYA90gAM2Y0OgdJt2eRCfhGGRRHe7iNX+VkMXQT365e0MdmucSez1bCkhTMkeR9/M+8SjixLkQcXsvXMdjFVCsDatZhyI4PtRnH3Wqfosc8SUe1JoUqx9xFCbL1iul1aOogFu4bECDcnOvNkHgfn2s/Oqt++9xlnWXEJYZeaChLhg9Sa812iJ6eZZUHCoaGn4adnane7akuf4pvdApwpoM8fVVF1uTogh2zf6CTX4b67UzimnjBRVllJ0Qc6Fw3MWqi0B/El0o3tZFCkG11tmTVXlE85PR5bpPDXdGwR1e/hbX82QAE7Wwxp3LdbxJd1QKwGGHQ2M7H1FanaDJu5XIeVUgYmh+xWZbxMRxgqYEsDifABwmcu+PIPY/gylFeDlEwxP2DB20lRRtVIwXkNfEXh0VsLpxeU2znHRfR84wJv472TI4Q+Imlx/Df3RWHYcQVTOMPSfMCbh0OxbUQx0M7tJbd9AJIJUeqeHvo5Yu3ghMA5Mo4hgZd8MkRXyUqGc1PUbjTnRg8xEcMWMMu5DfPVL1+m519BeJNl/74fxqhYwTaKBAmyJjPx3d7lWNzOHbM9oaImbhTnSEZYaihj8vq2hHRgjKd3373se2Ud1siTbqTNP/2TNmjzTX5s2xK0k+NprNhKVUrBlgeAkN3/hjqEzavw/LILxAPGK/++wYVmPM+PXDfH3LF6nd8NaLF4CSfao4byTpTTGns8bTs8+v+pijGYP4/4kN5aGjlW5gUvWzkgViah0h2G66xZqn6s4yExqrg4EXeJ8SnKNMT1Gqn/R91+Ys1auLIqPh80X4xcLwz+LQFyeIJ8NuwX+6mU7m+/9/ZwTXYDQWMdfKqJDtuj0BvbL7jSP3Y1tKWbFL0nmwi+yAS/C2IsM3NELELSnKzCXyBozZvHgi3MNE/5kIdihU30FPEIZgNJhC37lZvX9EmF6BvyRi0Gy8XjFydwaGPbl9uWicLHmipaRktkXPa2QeFA6feIMidO+4i6D4HdVOYEa/Nvsp0Bt20hAN3jI6na0LPOS+0q9+aHIZKrWjCnImqBwmWT/ibQnhZbys29PK1Lc3+b+UXBptkXqrPT89q7qzwlcodtF1IEY2MFdVsTH5/qs6avlTAw24wtdwQqOib0ddqX4LybbS5N5ab5eGqM9c/hh3eF5B1XsBSUPo3L9DSmbsn47s7k9H0HfpfBhEg0hdBYav+JpAOzF6Oioy4DsQWJjD2l0QRFueol/TwQ9sEnU9bL4/bzChtkXxUXlAwgpIPoz+/1hicD5i/4P0bA74f7PbSPODJnMkdKkz3QuGW8Hz0cuaClXN7Cxh1F/wlH3A4JuZ+X7gh8JcRPW5jU7AKRP67zCHhQrIBBLI+EdDD3NU0XNuLV5ff61Sy67n06GRXgHW6ABA'");
    
}
