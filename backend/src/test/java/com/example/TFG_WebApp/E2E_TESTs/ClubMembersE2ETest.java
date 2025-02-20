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
public class ClubMembersE2ETest {
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

        WebElement disciplinasOption = driver.findElement(By.xpath("//a[text()='Miembros del Club']"));
        disciplinasOption.click();

        String expectedUrl = "https://localhost/miembros";
        if (driver.getCurrentUrl().equals(expectedUrl)) {
            System.out.println("Test PASSED: Se accedió correctamente a Miembros del Club");
        } else {
            System.out.println("Test FAILED: No se accedió a Miembros del Club");
        }
    }

    @AfterAll
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLoadClubMembersPage() {
        assertTrue(driver.findElement(By.tagName("header")).isDisplayed());
        assertTrue(driver.findElement(By.className("trainers-list")).isDisplayed());
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
        assertTrue(driver.findElement(By.id("new-coach")).isDisplayed());
    }

    @Test
    public void testAddAndRemoveCoach() {
        login();
        navigateToPage();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        int initialCount = countAllElements("trainers-list", "member-item");

        // Add new coach
        driver.findElement(By.id("new-coach")).click();
        driver.findElement(By.id("firstName")).sendKeys("Test");
        driver.findElement(By.id("lastName")).sendKeys("Coach");
        driver.findElement(By.id("licenseNumber")).sendKeys("999999");

        // Select a discipline from the multiple select
        WebElement disciplineDropdown = driver.findElement(By.id("disciplines"));
        Select select = new Select(disciplineDropdown);
        select.selectByVisibleText("Velocidad");

        driver.findElement(By.id("submit-coach")).click();

        handleAlert(wait);

        int updatedCount = countAllElements("trainers-list", "member-item");
        assertEquals(initialCount+1 , updatedCount);

        // Delete coach
        deleteCoachFromProfile("999999");

        int finalCount = countAllElements("trainers-list", "member-item");
        assertEquals(initialCount, finalCount);
    }

    @Test
    public void testFilterCoaches() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement nameFilter = driver.findElement(By.id("license_form"));
        nameFilter.sendKeys("C1001");
        driver.findElement(By.id("filter-btm")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("member-item")));
        assertEquals(1, countAllElements("trainers-list", "member-item"));
    }

    @Test
    public void testPagination() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement pageIndicator = driver.findElement(By.xpath("//span[contains(text(), 'Página')]"));
        String initialPageText = pageIndicator.getText();

        // Verifica si el botón "Siguiente" está presente y habilitado
        List<WebElement> nextButtons = driver.findElements(By.id("next_btm"));
        if (nextButtons.isEmpty() || nextButtons.get(0).getAttribute("disabled") != null) {
            System.out.println("⚠ No pagination available, only one page exists.");
            return; // Salir del test si no hay más páginas
        }

        // Si hay más páginas, hacer clic en "Siguiente"
        WebElement nextButton = nextButtons.get(0);
        nextButton.click();

        // Esperar hasta que cambie el texto de la paginación
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
            total += driver.findElements(By.id(itemClass)).size();

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

    private void deleteElement(String name, String listId, String itemClass, String buttonText) {
        List<WebElement> items = driver.findElements(By.className(itemClass));
        for (WebElement item : items) {
            WebElement nameElement = item.findElement(By.tagName("h3"));
            if (nameElement.getText().contains(name)) {
                item.findElement(By.xpath(".//button[contains(text(),'" + buttonText + "')]")).click();
                handleAlert(new WebDriverWait(driver, Duration.ofSeconds(5)));
                return;
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

    private void deleteCoachFromProfile(String licenseNum) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        while (true) {
            List<WebElement> coaches = driver.findElements(By.id("member-item"));
            for (WebElement coach : coaches) {
                WebElement license = coach.findElement(By.id("license"));
                if (license.getText().contains(licenseNum)) {
                    license.click(); // Open the coach profile

                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("profile-header")));
                    assertTrue(driver.findElement(By.className("profile-header")).isDisplayed());

                    WebElement deleteButton = driver.findElement(By.id("delete-btm"));
                    assertTrue(deleteButton.isDisplayed()); // Ensure only admins can delete

                    deleteButton.click();
                    handleAlert(wait); // Handle confirmation alert

                    // Return to members list
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
                wait.until(ExpectedConditions.stalenessOf(coaches.get(0)));
            }
        }
    }

}
