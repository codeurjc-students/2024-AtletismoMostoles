package com.example.service1.E2E_TESTs;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventsE2ETest {

    private String https = "https://localhost:4200/";
    private WebDriver driver;

    @BeforeAll
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        options.setAcceptInsecureCerts(true);

        WebDriverManager.chromedriver().setup();

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @BeforeEach
    void navigateToPage() {
        driver.get(https);
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

        assertEquals(https+"eventos", driver.getCurrentUrl(), "Test FAILED: No se accedió a Eventos");
    }

    @AfterAll
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testLoadEventsPage() {
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
    void testLoginAndCheckUIChanges() {
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
        handleAlert(wait);
    }

    @Test
    void testAddAndRemoveEvent() {
        login();
        navigateToPage();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        int initialCount = countAllElements();

        WebElement addButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[contains(@class, 'mat-mdc-raised-button') and contains(@class, 'mat-primary') and .//span[contains(text(), 'Añadir Evento')]]")));
        addButton.click();

        driver.findElement(By.cssSelector("input[formControlName='name']")).sendKeys("Test Event");
        driver.findElement(By.cssSelector("input[formControlName='imageUrl']")).sendKeys("https://example.com/event.jpg");
        driver.findElement(By.cssSelector("input[formControlName='mapUrl']")).sendKeys("https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2953.492786758932!2d-3.712315124243198!3d40.44156655412768!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0xd4229eeeb8c3845%3A0x7e58365ea9c9b7dd!2sEstadio%20Atletismo%20Vallehermoso!5e1!3m2!1ses!2ses!4v1752403024730!5m2!1ses!2ses\" width=\"600\" height=\"450\" style=\"border:0;\" allowfullscreen=\"\" loading=\"lazy\" referrerpolicy=\"no-referrer-when-downgrade");
        driver.findElement(By.cssSelector("input[formControlName='date']")).sendKeys("10-06-2025");

        WebElement organizedByClubCheckbox = driver.findElement(By.cssSelector("mat-checkbox[formControlName='organizedByClub']"));
        if (!organizedByClubCheckbox.isSelected()) {
            organizedByClubCheckbox.click();
        }

        WebElement disciplineDropdown = driver.findElement(By.cssSelector("mat-select[formControlName='disciplines']"));
        disciplineDropdown.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-option")));
        driver.findElement(By.xpath("//mat-option/span[contains(text(), 'Jabalina')]")).click();

        disciplineDropdown.sendKeys(Keys.ESCAPE);

        WebElement saveButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[contains(@class, 'mat-mdc-raised-button') and contains(@class, 'mat-primary') and span[normalize-space()='Guardar Evento']]")));
        saveButton.click();

        handleAlert(wait);

        navigateToPage();

        int updatedCount = countAllElements();
        assertEquals(initialCount + 1, updatedCount);

        navigateToPage();

        deleteEventFromDetails("Test Event");

        int finalCount = countAllElements();
        assertEquals(initialCount, finalCount);
        WebElement logOutbutton = driver.findElement(By.cssSelector("button[mat-raised-button][color='warn']"));
        logOutbutton.click();

    }

    @Test
    void testPagination() {
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

    private int countAllElements() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        int total = 0;

        while (true) {
            // Esperar al grid de eventos
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("section.events mat-grid-list")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("section.events mat-grid-list")));

            // Contar eventos actuales
            List<WebElement> items = driver.findElements(By.cssSelector("section.events mat-grid-tile"));
            System.out.println("📄 Página actual: " + items.size() + " eventos encontrados.");
            total += items.size();

            // Buscar botón "Siguiente" real
            WebElement nextButton = driver.findElement(By.xpath("//button[contains(., 'Siguiente')]"));

            // Verificar si está deshabilitado (ya estás en la última página)
            String disabled = nextButton.getAttribute("disabled");
            if (disabled != null) {
                System.out.println("✅ Fin de páginas: botón 'Siguiente' deshabilitado.");
                break;
            }

            // Scroll y clic
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", nextButton);
            wait.until(ExpectedConditions.elementToBeClickable(nextButton));

            // Obtener el primer evento actual antes de cambiar de página
            WebElement firstItemBefore = items.get(0);

            nextButton.click();

            // Esperar a que se actualicen los eventos (cambia el primer elemento visible)
            wait.until(ExpectedConditions.stalenessOf(firstItemBefore));
        }

        System.out.println("🔢 Total de eventos: " + total);
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
