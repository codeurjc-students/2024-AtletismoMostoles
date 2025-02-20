package com.example.TFG_WebApp.E2E_TESTs;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DisciplinesE2ETest {
    private WebDriver driver;

    @BeforeAll
    public void setup() {
        String os = System.getProperty("os.name").toLowerCase();
        ChromeOptions options = new ChromeOptions();

        if (os.contains("win")) {
            System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\ChromeDriver\\chromedriver.exe");
        } else {
            System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
            options.addArguments("--headless"); // Ejecutar en modo headless en Linux
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
        }

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

        WebElement disciplinasOption = driver.findElement(By.xpath("//a[text()='Disciplinas']"));
        disciplinasOption.click();

        String expectedUrl = "https://localhost/disciplines";
        if (driver.getCurrentUrl().equals(expectedUrl)) {
            System.out.println("Test PASSED: Se accedió correctamente a Disciplinas");
        } else {
            System.out.println("Test FAILED: No se accedió a Disciplinas");
        }
    }

    @AfterAll
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testPageLoad() {
        assertTrue(driver.findElement(By.tagName("header")).isDisplayed());
        assertTrue(driver.findElement(By.id("discipline-list")).isDisplayed());
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
        assertTrue(driver.findElement(By.id("new-discipline")).isDisplayed());
    }

    @Test
    public void testAddAndRemoveDiscipline() {
        testLoginAndCheckUIChanges();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Get initial count of disciplines across all pages
        int initialCount = countAllDisciplines();
        System.out.println("🔎 Initial total count: " + initialCount);

        // Open the add discipline modal
        WebElement addDisciplineButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("new-discipline")));
        addDisciplineButton.click();

        // Fill the form
        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
        nameField.sendKeys("Test Discipline");
        driver.findElement(By.id("description")).sendKeys("Test description");
        driver.findElement(By.id("imageLink")).sendKeys("https://example.com/image.jpg");
        driver.findElement(By.id("submit-discipline")).click();

        // Handle alert
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            System.out.println("✅ Alert: " + alert.getText());
            alert.accept();
        } catch (NoAlertPresentException e) {
            System.out.println("⚠ No alert detected.");
        }

        // Wait and count again
        wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(By.id("discipline-list"))));
        navigateToPage();

        int updatedCount = countAllDisciplines();
        System.out.println("✅ Updated total count: " + updatedCount);
        assertEquals(initialCount + 1, updatedCount, "❌ The discipline was not added correctly.");

        // Delete the newly added discipline
        deleteDiscipline("Test Discipline");

        navigateToPage();
        // Final count check
        int finalCount = countAllDisciplines();
        System.out.println("✅ Final total count: " + finalCount);
        assertEquals(initialCount, finalCount, "❌ The discipline was not deleted correctly.");
    }

    @Test
    public void testPagination() {
        WebElement pageIndicator = driver.findElement(By.xpath("//span[contains(text(), 'Página')]"));
        String initialPage = pageIndicator.getText();

        WebElement nextButton = driver.findElement(By.id("next_btm"));
        nextButton.click();

        new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(pageIndicator, initialPage)));

        assertNotEquals(initialPage, pageIndicator.getText());
    }

    private int countAllDisciplines() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        int totalDisciplines = 0;

        while (true) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("discipline-list")));

            // Count disciplines in the current page
            List<WebElement> disciplines = driver.findElements(By.className("discipline-item"));
            totalDisciplines += disciplines.size();

            // Check if there's a next page
            List<WebElement> nextButtons = driver.findElements(By.id("next_btm"));
            if (nextButtons.isEmpty() || nextButtons.get(0).getAttribute("disabled") != null) {
                break; // Last page reached
            } else {
                nextButtons.get(0).click();
                wait.until(ExpectedConditions.stalenessOf(disciplines.get(0)));
            }
        }

        return totalDisciplines;
    }

    private void deleteDiscipline(String disciplineName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        while (true) {
            List<WebElement> disciplines = driver.findElements(By.className("discipline-item"));
            for (WebElement discipline : disciplines) {
                WebElement nameElement = discipline.findElement(By.tagName("h3"));
                if (nameElement.getText().contains(disciplineName)) {
                    WebElement deleteButton = discipline.findElement(By.xpath(".//button[contains(text(),'Eliminar')]"));
                    deleteButton.click();

                    try {
                        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                        System.out.println("✅ Delete Alert: " + alert.getText());
                        alert.accept();
                    } catch (NoAlertPresentException e) {
                        System.out.println("⚠ No delete alert detected.");
                    }

                    // ✅ Manejar la alerta de confirmación de eliminación después del proceso
                    try {
                        Alert confirmationAlert = wait.until(ExpectedConditions.alertIsPresent());
                        System.out.println("✅ Confirmation Alert: " + confirmationAlert.getText());
                        confirmationAlert.accept();
                    } catch (NoAlertPresentException e) {
                        System.out.println("⚠ No confirmation alert detected.");
                    }

                    wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(By.id("discipline-list"))));
                    return;
                }
            }

            List<WebElement> nextButtons = driver.findElements(By.id("next_btm"));
            if (nextButtons.isEmpty() || nextButtons.get(0).getAttribute("disabled") != null) {
                System.out.println("❌ Discipline not found.");
                return;
            } else {
                nextButtons.get(0).click();
                wait.until(ExpectedConditions.stalenessOf(disciplines.get(0)));
            }
        }
    }

}
