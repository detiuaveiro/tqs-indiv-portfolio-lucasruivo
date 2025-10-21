import org.slf4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.junit.jupiter.api.extension.ExtendWith;
import io.github.bonigarcia.seljup.SeleniumJupiter;

import static org.slf4j.LoggerFactory.getLogger;
import static org.assertj.core.api.Assertions.assertThat;



@ExtendWith(SeleniumJupiter.class)
class HelloWorldChromeSelJupTest {

    static final Logger log = getLogger(HelloWorldChromeSelJupTest.class);


    void test(WebDriver driver) {
        // Navigate to the initial page
        String sutUrl = "https://bonigarcia.dev/selenium-webdriver-java/";
        driver.get(sutUrl);

        // Verify the title of the initial page
        String title = driver.getTitle();
        log.debug("The title of {} is {}", sutUrl, title);
        assertThat(title).isEqualTo("Hands-On Selenium WebDriver with Java");

        // Navigate to the "Slow calculator" link
        driver.findElement(By.linkText("Slow calculator")).click();

        // Verify that the current URL is correct
        String currentUrl = driver.getCurrentUrl();
        log.debug("Navigated to URL: {}", currentUrl);
        assertThat(currentUrl).contains("slow-calculator");
    }
}