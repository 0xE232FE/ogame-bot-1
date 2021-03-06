package com.matejko.service.impl;

import com.matejko.model.common.TabEnum;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Mikołaj Matejko on 29.07.2017 as part of ogame-expander
 */
@Slf4j
public class WebActions {
    public final WebDriver webDriver;
    private final String mainUrl;

    public WebActions(final WebDriver webDriver, final String mainUrl) {
        this.webDriver = webDriver;
        this.mainUrl = "https://" + mainUrl;
    }

    public void goToHomePage() {
        webDriver.get(urlWithoutUniversum());
    }

    private String urlWithoutUniversum() {
        Pattern pattern = Pattern.compile("(https://)(s[0-9]+-)(.*)");
        Matcher matcher = pattern.matcher(mainUrl);
        matcher.find();
        return matcher.group(1) + matcher.group(3);
    }

    public void clickSpan(final String id) {
        webDriver.findElement(By.xpath("//span[contains(text(),'%s')]".formatted(id))).click();
    }

    public void clickButton(final String id) {
        webDriver.findElement(By.xpath(id)).click();
    }

    public void submit(final String id) {
        WebElement button = webDriver.findElement(By.id(id));
        button.submit();
    }

    public void setTextBox(String id, String value) {
        WebElement textBox = webDriver.findElement(By.name(id));
        textBox.sendKeys(value);
    }

    public void setSelectBox(final String id, final String key) {
        Select selectBox = new Select(webDriver.findElement(By.id(id)));
        selectBox.selectByValue(key);
    }

    public void goToTab(final TabEnum tab) {
        webDriver.get(String.format("%s/game/index.php?page=ingame&component=%s", mainUrl, tab.getUrl()));
    }

    public boolean clickBuildButton(final String expression) {
        List<WebElement> elements = webDriver.findElements(By.xpath(expression));
        return elements.stream()
                .findFirst()
                .map(element -> {
                    element.click();
                    return true;
                })
                .orElse(false);
    }

    public void sleep(final int timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            log.error("sleep", e);
        }
    }

    public void closePopup(final String className) {
        webDriver.findElements(By.className(className))
                .forEach(f -> {
                    JavascriptExecutor js = (JavascriptExecutor) webDriver;
                    js.executeScript(
                            "arguments[0].parentNode.removeChild(arguments[0])", f);
                });
    }

    public void switchWindows() {
        final var windowHandles = webDriver.getWindowHandles();
        final var currentWindowHandle = webDriver.getWindowHandle();

        windowHandles.stream()
                .filter(window -> !Objects.equals(window, currentWindowHandle))
                .findFirst()
                .ifPresent(window -> webDriver.switchTo().window(window));
    }
}
