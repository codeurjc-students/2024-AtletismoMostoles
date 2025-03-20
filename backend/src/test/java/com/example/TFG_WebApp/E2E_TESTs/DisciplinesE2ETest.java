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
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }
        options.addArguments("--headless");
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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[mat-icon-button]")));
        menu.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.cdk-overlay-container")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.cdk-overlay-backdrop")));

        WebElement disciplinesOption = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[routerLink='/disciplines']")));
        try {
            disciplinesOption.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", disciplinesOption);
        }

        assertEquals("https://localhost/disciplines", driver.getCurrentUrl(), "Test FAILED: No se accedió a Disciplinas");
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
        assertTrue(driver.findElement(By.cssSelector(".discipline-list")).isDisplayed());
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

        driver.findElement(By.cssSelector("button[mat-raised-button][color='accent']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-card.login-form")));

        driver.findElement(By.cssSelector("input[formControlName='username']")).sendKeys("admin");
        driver.findElement(By.cssSelector("input[formControlName='password']")).sendKeys("adminpass");
        driver.findElement(By.cssSelector("button[mat-raised-button][color='primary']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button[mat-raised-button][color='warn']")));
        navigateToPage();

        WebElement logOutbutton = driver.findElement(By.cssSelector("button[mat-raised-button][color='warn']"));
        assertTrue(logOutbutton.isDisplayed());
        assertTrue(driver.findElement(By.cssSelector(".add-discipline button")).isDisplayed());
        logOutbutton.click();
    }

    @Test
    public void testAddAndRemoveDiscipline() {
        login();
        navigateToPage();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        int initialCount = countAllDisciplines();
        System.out.println("🔎 Initial total count: " + initialCount);

        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".add-discipline button")));
        addButton.click();

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("mat-dialog-container")));

        driver.findElement(By.cssSelector("input[formControlName='name']")).sendKeys("Test Discipline");
        driver.findElement(By.cssSelector("input[formControlName='schedule']")).sendKeys("Lunes y Miércoles");
        driver.findElement(By.cssSelector("input[formControlName='imageLink']")).sendKeys("https://example.com/image.jpg");

        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'mat-mdc-raised-button') and contains(@class, 'mat-primary') and .//span[contains(text(), 'Guardar')]]")));
        saveButton.click();

        handleAlert(wait);

        navigateToPage();
        int updatedCount = countAllDisciplines();
        assertEquals(initialCount + 1, updatedCount, "❌ The discipline was not added correctly.");

        deleteDiscipline("Test Discipline");

        navigateToPage();
        int finalCount = countAllDisciplines();
        assertEquals(initialCount, finalCount, "❌ The discipline was not deleted correctly.");
        WebElement logOutbutton = driver.findElement(By.cssSelector("button[mat-raised-button][color='warn']"));
        logOutbutton.click();
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

    private void deleteDiscipline(String disciplineName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Esperar a que la lista de disciplinas esté cargada
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-grid-tile")));

        List<WebElement> disciplines = driver.findElements(By.cssSelector("mat-grid-tile"));
        for (WebElement discipline : disciplines) {
            try {
                WebElement nameElement = discipline.findElement(By.tagName("h3"));
                if (nameElement.getText().trim().equals(disciplineName)) {
                    System.out.println("✅ Disciplina encontrada: " + nameElement.getText());

                    // **Nuevo XPath más preciso para el botón "Eliminar" dentro del mat-card-actions**
                    WebElement deleteButton = discipline.findElement(By.xpath(".//mat-card-actions/button[contains(@color, 'warn')]"));

                    // **Esperar que el botón sea visible y clickeable**
                    wait.until(ExpectedConditions.elementToBeClickable(deleteButton));

                    // **Forzar scroll al botón si es necesario**
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", deleteButton);

                    // **Intentar hacer clic normalmente**
                    try {
                        deleteButton.click();
                    } catch (Exception e) {
                        // **Si el clic normal no funciona, forzar clic con JavaScript**
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteButton);
                    }

                    // **Manejar la alerta de confirmación**
                    handleAlert(wait);
                    return;
                }
            } catch (Exception e) {
                System.out.println("⚠ No se pudo encontrar el nombre o botón de la disciplina: " + disciplineName);
            }
        }
        System.out.println("❌ Disciplina no encontrada: " + disciplineName);
    }

    private int countAllDisciplines() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        int totalDisciplines = 0;

        while (true) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".discipline-list")));

            List<WebElement> disciplines = driver.findElements(By.cssSelector("mat-grid-tile"));
            totalDisciplines += disciplines.size();

            List<WebElement> nextButtons = driver.findElements(By.xpath("//button[contains(text(), 'Siguiente')]"));
            if (nextButtons.isEmpty() || nextButtons.get(0).getAttribute("disabled") != null) {
                break;
            } else {
                nextButtons.get(0).click();
                wait.until(ExpectedConditions.stalenessOf(disciplines.get(0)));
            }
        }

        return totalDisciplines;
    }

    private void handleAlert(WebDriverWait wait) {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            System.out.println("✅ Alert: " + alert.getText());
            alert.accept();
        } catch (Exception e) {
            System.out.println("⚠ No alert detected.");
        }
    }

    private void login() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.findElement(By.cssSelector("button[mat-raised-button][color='accent']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-card.login-form")));
        driver.findElement(By.cssSelector("input[formControlName='username']")).sendKeys("admin");
        driver.findElement(By.cssSelector("input[formControlName='password']")).sendKeys("adminpass");
        driver.findElement(By.cssSelector("button[mat-raised-button][color='primary']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button[mat-raised-button][color='warn']")));
    }
}
