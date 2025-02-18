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
public class AthletesE2ETest {
    private WebDriver driver;

    @BeforeAll
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\ChromeDriver\\chromedriver.exe");

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

        WebElement disciplinasOption = driver.findElement(By.xpath("//a[text()='Ranking']"));
        disciplinasOption.click();

        String expectedUrl = "https://localhost/ranking";
        if (driver.getCurrentUrl().equals(expectedUrl)) {
            System.out.println("Test PASSED: Se accedió correctamente a Ranking");
        } else {
            System.out.println("Test FAILED: No se accedió a Ranking");
        }
    }

    @AfterAll
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLoadRankingPage() {
        assertTrue(driver.findElement(By.tagName("header")).isDisplayed());
        assertTrue(driver.findElement(By.className("list-section")).isDisplayed());
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
        assertTrue(driver.findElement(By.cssSelector(".list-section button")).isDisplayed());
    }

    @Test
    public void testAddAndRemoveAthlete() {
        login();
        navigateToPage();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        int initialCount = countAllElements("list-section", "athlete-row");

        // Open new athlete modal
        driver.findElement(By.cssSelector(".list-section button")).click();

        // Fill the form fields
        driver.findElement(By.id("licenseNumber")).sendKeys("999999");
        driver.findElement(By.id("firstName")).sendKeys("Test");
        driver.findElement(By.id("lastName")).sendKeys("Athlete");
        driver.findElement(By.id("birthDate")).sendKeys("2000-05-10");

        // Select a coach
        WebElement coachDropdown = driver.findElement(By.id("coach"));
        Select selectCoach = new Select(coachDropdown);
        selectCoach.selectByVisibleText("Clara Díaz");

        // Select multiple disciplines
        WebElement disciplinesDropdown = driver.findElement(By.id("disciplines"));
        Select selectDisciplines = new Select(disciplinesDropdown);
        selectDisciplines.selectByVisibleText("Velocidad");
        //selectDisciplines.selectByVisibleText("Salto de longitud");

        // Submit the form
        driver.findElement(By.cssSelector(".modal-footer .btn-primary")).click();

        handleAlert(wait);

        int updatedCount = countAllElements("list-section", "athlete-row");
        assertEquals(initialCount + 1, updatedCount);

        // Delete athlete
        deleteAthleteFromProfile("999999");

        int finalCount = countAllElements("list-section", "athlete-row");
        assertEquals(initialCount, finalCount);
    }

    @Test
    public void testFilterAthletes() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        driver.findElement(By.id("licenseNumber_form")).sendKeys("A2001");
        driver.findElement(By.id("filter-button")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("athlete-row")));
        assertEquals(1, countAllElements("list-section", "athlete-row"));
    }

    @Test
    public void testPagination() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement pageIndicator = driver.findElement(By.xpath("//span[contains(text(), 'Página')]"));
        String initialPageText = pageIndicator.getText();

        List<WebElement> nextButtons = driver.findElements(By.xpath("//button[contains(text(), 'Siguiente')]"));
        if (nextButtons.isEmpty() || nextButtons.get(0).getAttribute("disabled") != null) {
            System.out.println("⚠ No pagination available, only one page exists.");
            return;
        }

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
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(listId)));
            total += driver.findElements(By.id(itemClass)).size();

            List<WebElement> nextButtons = driver.findElements(By.xpath("//button[contains(text(), 'Siguiente')]"));
            if (nextButtons.isEmpty() || nextButtons.get(0).getAttribute("disabled") != null) {
                break;
            } else {
                nextButtons.get(0).click();
                wait.until(ExpectedConditions.stalenessOf(driver.findElements(By.id(itemClass)).get(0)));
            }
        }

        return total;
    }

    private void deleteAthleteFromProfile(String licenseNum) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        while (true) {
            List<WebElement> athletes = driver.findElements(By.id("athlete-row"));
            for (WebElement athlete : athletes) {
                WebElement license = athlete.findElement(By.id("license"));
                if (license.getText().contains(licenseNum)) {
                    license.click(); // Open the athlete profile

                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("profile-header")));
                    assertTrue(driver.findElement(By.className("profile-header")).isDisplayed());

                    WebElement deleteButton = driver.findElement(By.id("delete-btm"));
                    assertTrue(deleteButton.isDisplayed()); // Ensure only admins can delete

                    deleteButton.click();
                    handleAlert(wait); // Handle confirmation alert

                    // Return to Ranking list
                    handleAlert(wait);
                    navigateToPage();
                    return;
                }
            }

            // Check for pagination
            List<WebElement> nextButtons = driver.findElements(By.id("next_btm"));
            if (nextButtons.isEmpty() || nextButtons.get(0).getAttribute("disabled") != null) {
                System.out.println("❌ Coach not found.");
                return;
            } else {
                nextButtons.get(0).click();
                wait.until(ExpectedConditions.stalenessOf(athletes.get(0)));
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

