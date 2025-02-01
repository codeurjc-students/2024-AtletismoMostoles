package com.example.TFG_WebApp.E2E_TESTs;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.Set;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DisciplinesE2ETest {
    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    public static void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--allow-insecure-localhost");
        options.addArguments("--headless");
        options.addArguments("--disable-blink-features=BlockCredentialedSubresources");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }


    @BeforeEach
    public void loginAsAdmin() {
        driver.get("https://localhost:4200/login");
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("adminpass");
        WebElement button = driver.findElement(By.id("login-button"));
        button.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("index_title")));

        Set<Cookie> cookies = driver.manage().getCookies();
        for (Cookie cookie : cookies) {
            driver.manage().addCookie(cookie);
        }
        driver.navigate().refresh();

    }

    @Test
    @Order(1)
    public void testAccessDisciplineList() {
        driver.get("https://localhost:4200/disciplines");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("discipline-list")));
        Assertions.assertTrue(driver.findElement(By.id("discipline-list")).isDisplayed(), "La lista de disciplinas no se muestra");
    }

    @Test
    @Order(2)
    public void testCreateDiscipline() {
        driver.get("https://localhost:4200/disciplines");
        Set<Cookie> c = driver.manage().getCookies();
        System.out.println(c.toString());
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-discipline")));
        driver.findElement(By.id("new-discipline")).click();
        driver.findElement(By.id("name")).sendKeys("Salto de Longitud");
        driver.findElement(By.id("description")).sendKeys("Lunes 16:00 - 18:00");
        driver.findElement(By.id("submit-discipline")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[contains(text(),'Salto de Longitud')]")));
        Assertions.assertTrue(driver.findElement(By.xpath("//td[contains(text(),'Salto de Longitud')]")).isDisplayed(), "La disciplina no se creó correctamente");
    }

    @Test
    @Order(3)
    public void testEditDiscipline() {
        driver.get("https://localhost:4200/disciplines");
        driver.findElement(By.xpath("//td[contains(text(),'Salto de Longitud')]/following-sibling::td/button[@id='edit-discipline']")).click();
        WebElement descriptionField = driver.findElement(By.id("discipline-description"));
        descriptionField.clear();
        descriptionField.sendKeys("Martes 18:00 - 20:00");
        driver.findElement(By.id("submit-discipline")).click();

        wait.until(ExpectedConditions.textToBe(By.xpath("//td[contains(text(),'Salto de Longitud')]/following-sibling::td[@id='discipline-description']"), "Martes 18:00 - 20:00"));
        Assertions.assertEquals("Martes 18:00 - 20:00", driver.findElement(By.xpath("//td[contains(text(),'Salto de Longitud')]/following-sibling::td[@id='discipline-description']")).getText(), "La disciplina no se actualizó correctamente");
    }

    @Test
    @Order(4)
    public void testDeleteDiscipline() {
        driver.findElement(By.xpath("//td[contains(text(),'Salto de Longitud')]/following-sibling::td/button[@id='delete-discipline']")).click();
        driver.switchTo().alert().accept();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//td[contains(text(),'Salto de Longitud')]")));

        Assertions.assertEquals(0, driver.findElements(By.xpath("//td[contains(text(),'Salto de Longitud')]")).size(), "La disciplina no fue eliminada correctamente");
    }

    @Test
    @Order(5)
    public void testUnauthorizedAccess() {
        driver.get("https://localhost:4200/logout");
        driver.get("https://localhost:4200/disciplines");
        Assertions.assertNotEquals("https://localhost:4200/disciplines", driver.getCurrentUrl(), "Un usuario no autorizado accedió a la página de disciplinas");
    }

    @AfterAll
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
