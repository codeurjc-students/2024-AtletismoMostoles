package com.example.TFG_WebApp.E2E_TESTs;

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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClubMembersE2ETest {
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
        driver.get("https://localhost/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement menuButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[mat-icon-button]")));
        menuButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.cdk-overlay-container")));

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.cdk-overlay-backdrop")));

        WebElement rankingOption = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[routerLink='/miembros']")));

        try {
            rankingOption.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", rankingOption);
        }

        String expectedUrl = "https://localhost/miembros";
        if (driver.getCurrentUrl().equals(expectedUrl)) {
            System.out.println("Test PASSED: Se accedió correctamente a ClubMembers");
        } else {
            System.out.println("Test FAILED: No se accedió a ClubMembers");
        }
    }

    @AfterAll
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testLoadClubMembersPage() {
        assertTrue(driver.findElement(By.tagName("header")).isDisplayed());
        assertTrue(driver.findElement(By.id("list-section")).isDisplayed());
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
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button[mat-raised-button][color='warn']")));

        WebElement logoutButton = driver.findElement(By.cssSelector("button[mat-raised-button][color='warn']"));
        assertTrue(logoutButton.isDisplayed());
        assertTrue(driver.findElement(By.cssSelector(".list-section button")).isDisplayed());
        logoutButton.click();
    }

    @Test
    void testAddAndRemoveCoach() {
        login();
        navigateToPage();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        int initialCount = countAllElements("list-section");

        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".list-section button:not([disabled])")));
        addButton.click();

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("mat-dialog-container")));

        driver.findElement(By.cssSelector("input[formControlName='licenseNumber']")).sendKeys("999999");
        driver.findElement(By.cssSelector("input[formControlName='firstName']")).sendKeys("Test");
        driver.findElement(By.cssSelector("input[formControlName='lastName']")).sendKeys("Coach");

        WebElement disciplineDropdown = driver.findElement(By.cssSelector("mat-select[formControlName='disciplines']"));
        disciplineDropdown.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("mat-option")));
        driver.findElement(By.xpath("//mat-option/span[contains(text(), 'Velocidad')]")).click();

        disciplineDropdown.sendKeys(Keys.ESCAPE);

        wait.until(ExpectedConditions.visibilityOf(modal));

        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'mat-mdc-raised-button') and contains(@class, 'mat-primary') and .//span[contains(text(), 'Guardar')]]")));
        saveButton.click();

        handleAlert(wait);

        navigateToPage();
        int updatedCount = countAllElements("list-section");
        assertEquals(initialCount + 1, updatedCount);

        deleteCoachFromProfile("999999");

        int finalCount = countAllElements("list-section");
        assertEquals(initialCount, finalCount);
        WebElement logOutbutton = driver.findElement(By.cssSelector("button[mat-raised-button][color='warn']"));
        logOutbutton.click();
    }

    @Test
    void testFilterAthletes() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        driver.findElement(By.cssSelector("input[name='licenseNumber']")).sendKeys("C1001");
        driver.findElement(By.cssSelector("button[mat-raised-button][color='primary']")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("mat-table mat-row")));
        assertEquals(1, countAllElements("list-section"));
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

    private void deleteCoachFromProfile(String licenseNum) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        while (true) {
            List<WebElement> rows = driver.findElements(By.cssSelector("mat-table mat-row"));
            for (WebElement row : rows) {
                WebElement license = row.findElement(
                        By.xpath(".//mat-cell[contains(text(), '" + licenseNum + "')]")
                );
                if (license != null && license.getText().contains(licenseNum)) {
                    license.click();

                    wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.className("profile-card")
                    ));

                    WebElement deleteButton = wait.until(
                            ExpectedConditions.elementToBeClickable(
                                    By.xpath("//button[normalize-space()='Eliminar Perfil']")
                            )
                    );
                    ((JavascriptExecutor) driver)
                            .executeScript("arguments[0].scrollIntoView({block: 'center'});", deleteButton);
                    deleteButton.click();

                    wait.until(ExpectedConditions.alertIsPresent());
                    Alert alert = driver.switchTo().alert();
                    alert.accept();

                    wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.id("list-section")
                    ));
                    return;
                }
            }

            List<WebElement> nextButtons = driver.findElements(By.id("next_btm"));
            if (nextButtons.isEmpty() || nextButtons.get(0).getAttribute("disabled") != null) {
                System.out.println("❌ Coach not found.");
                return;
            } else {
                nextButtons.get(0).click();
                wait.until(ExpectedConditions.stalenessOf(rows.get(0)));
            }
        }
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

    private int countAllElements(String listId) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        int total = 0;

        while (true) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(listId)));
            total += driver.findElements(By.cssSelector(".clickable-row")).size();

            List<WebElement> nextButtons = driver.findElements(By.xpath("//button[contains(text(), 'Siguiente')]"));
            if (nextButtons.isEmpty()) {
                break;
            }

            WebElement nextButton = nextButtons.get(0);
            if (!nextButton.isEnabled()) {
                break;
            } else {
                nextButton.click();
                List<WebElement> rows = driver.findElements(By.cssSelector(".clickable-row"));
                if (!rows.isEmpty()) {
                    wait.until(ExpectedConditions.stalenessOf(rows.get(0)));
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
