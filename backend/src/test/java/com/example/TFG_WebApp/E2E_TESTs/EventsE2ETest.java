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
public class EventsE2ETest {
    private WebDriver driver;

    @BeforeAll
    public void setup() {
        String os = System.getProperty("os.name").toLowerCase();
        ChromeOptions options = new ChromeOptions();

        if (os.contains("win")) {
            System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\ChromeDriver\\chromedriver.exe");
        } else {
            System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
            options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu", "--window-size=1920,1080");
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
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement menuButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[mat-icon-button]")));
        menuButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.cdk-overlay-container")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.cdk-overlay-backdrop")));

        WebElement eventsOption = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[routerLink='/eventos']")));
        try {
            eventsOption.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", eventsOption);
        }

        assertEquals("https://localhost/eventos", driver.getCurrentUrl(), "Test FAILED: No se accedió a Eventos");
    }

    @AfterAll
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLoadEventsPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Esperar a que la cabecera, el listado de eventos y el footer sean visibles
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("header")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-grid-list"))); // Reemplazo de .events-list
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("footer")));

        // Verificar que los elementos están presentes en la página
        assertTrue(driver.findElement(By.tagName("header")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("mat-grid-list")).isDisplayed());
        assertTrue(driver.findElement(By.tagName("footer")).isDisplayed());
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
        WebElement addEventButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(@class, 'mat-mdc-raised-button') and contains(@class, 'mat-primary') and span[contains(text(), 'Añadir Evento')]]")));
        assertTrue(addEventButton.isDisplayed());
        logOutbutton.click();
    }

    @Test
    public void testAddAndRemoveEvent() {
        login();
        navigateToPage();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        int initialCount = countAllElements("events-list", "event-card");

        WebElement addButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[contains(@class, 'mat-mdc-raised-button') and contains(@class, 'mat-primary') and .//span[contains(text(), 'Añadir Evento')]]")));
        addButton.click();

        driver.findElement(By.cssSelector("input[formControlName='name']")).sendKeys("Test Event");
        driver.findElement(By.cssSelector("input[formControlName='imageUrl']")).sendKeys("https://example.com/event.jpg");
        driver.findElement(By.cssSelector("input[formControlName='mapUrl']")).sendKeys("https://maps.google.com/example");
        driver.findElement(By.cssSelector("input[formControlName='date']")).sendKeys("10-06-2025");

        WebElement organizedByClubCheckbox = driver.findElement(By.cssSelector("mat-checkbox[formControlName='organizedByClub']"));
        if (!organizedByClubCheckbox.isSelected()) {
            organizedByClubCheckbox.click();
        }

        WebElement disciplineDropdown = driver.findElement(By.cssSelector("mat-select[formControlName='disciplines']"));
        disciplineDropdown.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-option")));
        driver.findElement(By.xpath("//mat-option/span[contains(text(), 'Velocidad')]")).click();

        disciplineDropdown.sendKeys(Keys.ESCAPE);

        WebElement saveButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[contains(@class, 'mat-mdc-raised-button') and contains(@class, 'mat-primary') and span[normalize-space()='Guardar Evento']]")));
        saveButton.click();

        handleAlert(wait);

        navigateToPage();

        int updatedCount = countAllElements("events-list", "event-card");
        assertEquals(initialCount + 1, updatedCount);

        deleteEventFromDetails("Test Event");

        int finalCount = countAllElements("events-list", "event-card");
        assertEquals(initialCount, finalCount);
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

    private void deleteEventFromDetails(String eventName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("mat-grid-list")));

        List<WebElement> events = driver.findElements(By.cssSelector("mat-grid-tile"));
        for (WebElement event : events) {
            try {
                WebElement nameElement = event.findElement(By.tagName("h3"));
                if (nameElement.getText().contains(eventName)) {
                    System.out.println("✅ Evento encontrado: " + nameElement.getText());

                    WebElement deleteButton = event.findElement(By.xpath(".//button[contains(@class, 'mat-warn')]"));


                    wait.until(ExpectedConditions.elementToBeClickable(deleteButton));

                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", deleteButton);
                    try {
                        deleteButton.click();
                    } catch (Exception e) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteButton);
                    }

                    handleAlert(wait);

                    wait.until(ExpectedConditions.stalenessOf(event));

                    System.out.println("✅ Evento eliminado correctamente.");
                    return;
                }
            } catch (Exception e) {
                System.out.println("⚠ No se pudo encontrar el nombre o botón de eliminar del evento: " + eventName);
            }
        }
        System.out.println("❌ Evento no encontrado: " + eventName);
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

    private int countAllElements(String listId, String itemClass) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        int total = 0;

        while (true) {
            // **Esperar a que el contenedor de la lista esté presente y visible**
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("mat-grid-list")));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-grid-list")));
            } catch (Exception e) {
                System.out.println("⚠ No se encontró el contenedor de eventos.");
                return total;
            }

            // **Esperar que al menos un elemento de la lista esté visible**
            List<WebElement> items = driver.findElements(By.cssSelector("mat-grid-tile"));
            if (!items.isEmpty()) {
                total += items.size();
            }

            // **Buscar botón "Siguiente"**
            List<WebElement> nextButtons = driver.findElements(By.xpath("//button[contains(text(), 'Siguiente')]"));

            if (nextButtons.isEmpty() || !nextButtons.get(0).isDisplayed() || !nextButtons.get(0).isEnabled()) {
                break; // **Salir si no hay más páginas**
            } else {
                // **Hacer clic en "Siguiente" y esperar a que la lista se actualice**
                WebElement nextButton = nextButtons.get(0);
                wait.until(ExpectedConditions.elementToBeClickable(nextButton));

                try {
                    nextButton.click();
                } catch (Exception e) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextButton);
                }

                // **Esperar a que los elementos de la lista se recarguen**
                if (!items.isEmpty()) {
                    wait.until(ExpectedConditions.stalenessOf(items.get(0)));
                }
            }
        }

        return total;
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
