package com.dos.test_assess.enums;

import java.io.Serializable;

/**
 * @author Garth Bosch
 * @date 11 April 2017
 */
public enum PageElements implements Serializable {
    /**
     * *** For all pages ****
     */
    ArticleTitleText("//h2[@class='esc-lead-article-title']/a/span[@class='titletext']", "Text Of The Article"),
    EventListContent("//div[@class='eventListContent']/div/div[@class='collapsablePanel']", "Live Games On Page");

    private final String elementId;

    private final String elementNameOnPage;

    PageElements(String elementId, String elementNameOnPage) {
        this.elementId = elementId;
        this.elementNameOnPage = elementNameOnPage;
    }

    public String getElementId() {
        return elementId;
    }

    public String getElementNameOnPage() {
        return elementNameOnPage;
    }
}