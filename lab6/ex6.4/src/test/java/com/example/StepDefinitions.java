package com.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebElement;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class StepDefinitions {

    private static WebDriver driver;
    private String pageUrl;

    @Before
    public void setUp() {
        if (driver == null) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions opts = new ChromeOptions();
            // run headless in CI; remove if you want visible browser locally
            opts.addArguments("--headless=new");
            // allow remote origins for recent ChromeDriver/Chrome combinations
            opts.addArguments("--remote-allow-origins=*");
            opts.addArguments("--no-sandbox");
            opts.addArguments("--disable-dev-shm-usage");
            driver = new ChromeDriver(opts);
        }
        File f = new File("src/test/resources/web/library.html");
        pageUrl = f.getAbsoluteFile().toURI().toString();
    }

    @After
    public void tearDown() {
        // keep driver alive between scenarios for speed; if you prefer close here, uncomment
        // if (driver != null) { driver.quit(); driver = null; }
    }

    @ParameterType("([0-9]{4}-[0-9]{2}-[0-9]{2})")
    public LocalDate iso8601Date(String date) {
        return LocalDate.parse(date);
    }

    @Given("the library webapp is open")
    public void theLibraryWebappIsOpen() {
        driver.navigate().to(pageUrl);
    }

    @Given("the following books exist on the page:")
    public void theFollowingBooksExistOnThePage(DataTable table) {
        List<Map<String,String>> rows = table.asMaps(String.class, String.class);
        // Build a JS array literal and call populateBooks
        StringBuilder js = new StringBuilder();
        js.append("populateBooks([");
        for (Map<String,String> r : rows) {
            String title = escapeJs(r.getOrDefault("title",""));
            String author = escapeJs(r.getOrDefault("author",""));
            String category = escapeJs(r.getOrDefault("category",""));
            String published = escapeJs(r.getOrDefault("published",""));
            js.append("{title:'").append(title).append("',author:'").append(author).append("',category:'")
              .append(category).append("',published:'").append(published).append("'},");
        }
        js.append("]);");
        ((JavascriptExecutor)driver).executeScript(js.toString());
    }

    private String escapeJs(String s) {
        if (s == null) return "";
        return s.replace("\\","\\\\").replace("'","\\'" ).replace("\n","\\n");
    }

    @When("I search for books by author {string} using the UI")
    public void iSearchForBooksByAuthorUsingTheUI(String author) {
        driver.findElement(By.id("authorInput")).clear();
        driver.findElement(By.id("authorInput")).sendKeys(author);
        driver.findElement(By.id("searchBtn")).click();
    }

    @When("I search for books in the {string} category using the UI")
    public void iSearchForBooksInTheCategoryUsingTheUI(String category) {
        driver.findElement(By.id("categoryInput")).clear();
        driver.findElement(By.id("categoryInput")).sendKeys(category);
        driver.findElement(By.id("searchBtn")).click();
    }

    
    @When("I search for books published between {iso8601Date} and {iso8601Date} using the UI")
    public void iSearchForBooksPublishedBetweenUsingTheUI(LocalDate from, LocalDate to) {
        // set date inputs via JavaScript to avoid input-type=date locale/sendKeys issues
        String js = "document.getElementById('fromDate').value = '" + from.toString() + "';"
                  + "document.getElementById('toDate').value = '" + to.toString() + "';";
        ((JavascriptExecutor)driver).executeScript(js);
        driver.findElement(By.id("searchBtn")).click();
    }

    @Then("I should see {int} result\\(s\\) in the UI")
    public void iShouldSeeResultsInTheUI(Integer expected) {
        List<WebElement> rows = driver.findElements(By.cssSelector("#books tr"));
        long visible = rows.stream().filter(r -> r.isDisplayed()).count();
        assertEquals(expected.intValue(), (int)visible);
    }

}

