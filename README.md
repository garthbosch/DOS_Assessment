# DOS_Assessment

Requirements
============
EXTERNAL (To be completed outside the office)
Key:
Programming Language – C# or Java
Automation Web Testing Tool – Webdriver (Selenium)
BDD Framework – Your own choice
Time: Should not exceed 4 hours
Scenario – A company and their QA engineers are new to Automation testing and will like to run multiple tests across different sites, whilst connecting to a single base/framework.
Tests should be able to run on a minimum of 1 current web browser and be able to output results in a readable format to the business.
The Base/Framework should be able to open a new browser session with cache & cookies cleared along with closing & terminating the session after test(s) have ran.
 
Test(s) to run:
A.      Go to http://news.google.com/ and print out all the headlines displayed on the page.
B.      Go to https://sports.betway.com/ and print out all the live games displayed on the page.

 
1.       Create the above scenario using tools/software from the key provided
2.       In your own words, describe what makes a good automation base/framework.

Solution
========
Technologies Stack:
-------------------
1)  Programming Language    : Java v1.8.0_91
2)  Automation Test Tool    : Selenium WebDriver v3.3.1
3)  Testing Framework       : TestNG v6.8
4)  Browser Driver          : Chrome Driver v2.29.461591
5)  Internet Browser        : Chrome v57.0.2987.133
6)  IDE used                : Eclipse Version: Luna Service Release 1a (4.4.1)

Dependancies:
-------------
All the dependance libraries can be found under "./lib".

Output Files:
-------------
1)  TestNG Reports can be found under "./test-output".
2)  Screenshots can be found under "./logs/GoogleNews_CollectAndDisplayData_AutomationTesting" and "./logs/SportsBetway_CollectAndDisplayData_AutomationTesting".
3)  Print outs of tests can be founf under "./logs/test/2017_04_11". This is one log file for both tests.

Source Files:
-------------
1) com.dos.test_assess.enums.PageElements.java              : This is an ENUM class that contains all the page elements.
2) com.dos.test_assess.utils.AbstractBaseClass.java         : This is an Abstract class that contains all the common variables. 
3) com.dos.test_assess.utils.SeleniumWebDriverUtils.java    : This is a wrapper for selenium commands.
4) com.dos.test_assess.test.CollectAndDisplayData.java      : This is the actual test that gets called by the xml script. It does most of the test logic.

Test Script:
------------
1)  The test script "assessment_testsuite.xml" can be found under "./testsuites". This script is an xml file which contains two tests that covers Test Run A and B in the requirements.

Setup and Execution:
--------------------
1)  Create a java project on eclipse - the name must be DOS_Assessment.
2)  The location should be where you check the code out to.
3)  Once the project has been created make sure you configure your build path with the correct dependancies. See Dependancies section.
4)  After successful setup of the project right-click the test script "assessment_testsuite.xml" and run it as a TestNG Suite.

Conclusion:
-----------
The following are good characteristics of an automation test framework:
1)  It should be robust.
2)  Troubleshooting should be easy.
3)  Where possible it should be able to run against most applications.
4)  Script creation should be easy.
5)  Maintenance should be easy.
6)  Good reporting capabilities.
7)  Easy integration with other tools.