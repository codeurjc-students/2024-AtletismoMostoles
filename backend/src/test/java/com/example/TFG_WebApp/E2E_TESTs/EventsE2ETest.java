package com.example.TFG_WebApp.E2E_TESTs;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EventsE2ETest {
    private WebDriver driver;

    @BeforeAll
    public void setup() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\ChromeDriver\\chromedriver.exe");
        } else {
            System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        }
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        options.setAcceptInsecureCerts(true);

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @BeforeEach
    public void navigateToPage() {
        driver.get("https://localhost/");

        WebElement menu = driver.findElement(By.className("menu-icon"));
        menu.click();

        WebElement disciplinasOption = driver.findElement(By.xpath("//a[text()='Eventos']"));
        disciplinasOption.click();

        String expectedUrl = "https://localhost/eventos";
        if (driver.getCurrentUrl().equals(expectedUrl)) {
            System.out.println("Test PASSED: Se accedió correctamente a Eventos");
        } else {
            System.out.println("Test FAILED: No se accedió a Eventos");
        }
    }

    @AfterAll
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLoadEventsPage() {
        assertTrue(driver.findElement(By.tagName("header")).isDisplayed());
        assertTrue(driver.findElement(By.className("events-list")).isDisplayed());
        assertTrue(driver.findElement(By.tagName("footer")).isDisplayed());
    }

    @Test
    public void testNavigationLinks() {
        assertEquals(driver.findElement(By.linkText("Inicio")).getAttribute("href"), "https://localhost/");
        assertEquals(driver.findElement(By.linkText("Miembros del Club")).getAttribute("href"), "https://localhost/miembros");
        assertEquals(driver.findElement(By.linkText("Ranking")).getAttribute("href"), "https://localhost/ranking");
        assertEquals(driver.findElement(By.linkText("Eventos")).getAttribute("href"), "https://localhost/eventos");
        assertEquals(driver.findElement(By.linkText("Calendario")).getAttribute("href"), "https://localhost/calendario");
    }

    @Test
    public void testLoginAndCheckUIChanges() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        driver.findElement(By.id("login-button")).click();
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("adminpass");
        driver.findElement(By.id("login-button")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout-button")));
        navigateToPage();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout-button")));
        assertTrue(driver.findElement(By.id("logout-button")).isDisplayed());
        assertTrue(driver.findElement(By.id("add-event")).isDisplayed());
    }

    @Test
    public void testAddAndRemoveEvent() {
        login();
        navigateToPage();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        int initialCount = countAllElements("events-list", "event-card");

        // Open the add event form
        driver.findElement(By.id("add-event")).click();

        // Fill the form fields
        driver.findElement(By.id("name")).sendKeys("Test Event");
        driver.findElement(By.id("imageUrl")).sendKeys("https://example.com/event.jpg");
        driver.findElement(By.id("mapUrl")).sendKeys("https://maps.google.com/example");
        driver.findElement(By.id("date")).sendKeys("10-06-2025");

        // Check the "Organizado por el Club" checkbox
        WebElement organizedByClubCheckbox = driver.findElement(By.cssSelector("input[formControlName='organizedByClub']"));
        if (!organizedByClubCheckbox.isSelected()) {
            organizedByClubCheckbox.click();
        }

        // Select multiple disciplines
        WebElement disciplinesDropdown = driver.findElement(By.id("disciplines"));
        Select select = new Select(disciplinesDropdown);
        select.selectByVisibleText("Velocidad");
        //select.selectByVisibleText("Salto de longitud");

        // Submit the form
        WebElement submitButton = driver.findElement(By.id("submit-event"));
        assertTrue(submitButton.isEnabled(), "❌ Submit button should be enabled when the form is valid.");
        submitButton.click();

        handleAlert(wait);

        navigateToPage();

        int updatedCount = countAllElements("events-list", "event-card");
        assertEquals(initialCount + 1, updatedCount);

        // Delete event from event details page
        deleteEventFromDetails("Test Event");

        int finalCount = countAllElements("events-list", "event-card");
        assertEquals(initialCount, finalCount);
    }

    /*@Test
    public void testFilterEvents() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement filterDropdown = driver.findElement(By.id("eventFilter"));
        Select select = new Select(filterDropdown);
        select.selectByVisibleText("Club");

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("events-list"), "Organizamos este evento"));
    }*/

    @Test
    public void testPagination() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement pageIndicator = driver.findElement(By.xpath("//span[contains(text(), 'Página')]"));
        String initialPageText = pageIndicator.getText();

        // Verifica si el botón "Siguiente" está presente y habilitado
        List<WebElement> nextButtons = driver.findElements(By.id("next_btm"));
        if (nextButtons.isEmpty() || nextButtons.get(0).getAttribute("disabled") != null) {
            System.out.println("⚠ No pagination available, only one page exists.");
            return;
        }

        // Si hay más páginas, hacer clic en "Siguiente"
        WebElement nextButton = nextButtons.get(0);
        nextButton.click();

        wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(pageIndicator, initialPageText)));

        assertNotEquals(initialPageText, pageIndicator.getText(), "❌ The page number did not change after clicking 'Next'.");
    }

    private void handleAlert(WebDriverWait wait) {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            System.out.println("✅ Alert: " + alert.getText());
            alert.accept();
        } catch (NoAlertPresentException e) {
            System.out.println("⚠ No alert detected.");
        }
    }

    private int countAllElements(String listId, String itemClass) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        int total = 0;

        while (true) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(listId)));
            total += driver.findElements(By.className(itemClass)).size();

            List<WebElement> nextButtons = driver.findElements(By.id("next_btm"));
            if (nextButtons.isEmpty() || nextButtons.get(0).getAttribute("disabled") != null) {
                break;
            } else {
                nextButtons.get(0).click();
                wait.until(ExpectedConditions.stalenessOf(driver.findElements(By.className(itemClass)).get(0)));
            }
        }

        return total;
    }

    private void deleteEventFromDetails(String eventName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        while (true) {
            List<WebElement> events = driver.findElements(By.className("event-card"));
            for (WebElement event : events) {
                WebElement nameElement = event.findElement(By.tagName("h3"));
                if (nameElement.getText().contains(eventName)) {
                    WebElement deleteButton = event.findElement(By.id("delete-btm"));

                    if (!deleteButton.isDisplayed()) {
                        System.out.println("❌ Delete button is not visible (user may not be logged in).");
                        return;
                    }

                    deleteButton.click();

                    handleAlert(wait);

                    wait.until(ExpectedConditions.stalenessOf(event));
                    System.out.println("✅ Event successfully deleted.");
                    return;
                }
            }

            List<WebElement> nextButtons = driver.findElements(By.id("next_btm"));
            if (nextButtons.isEmpty() || nextButtons.get(0).getAttribute("disabled") != null) {
                System.out.println("❌ Event not found in any page.");
                return;
            } else {
                nextButtons.get(0).click();
                wait.until(ExpectedConditions.stalenessOf(events.get(0)));
            }
        }
    }

    private void login() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.findElement(By.id("login-button")).click();
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("adminpass");
        driver.findElement(By.id("login-button")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout-button")));
    }
}
