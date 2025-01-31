package com.example.TFG_WebApp.API_REST_TESTs;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ResultRestControllerTest {

    private static int resultId;
    private static String authToken;

    @BeforeAll
    public static void setup() {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = "https://localhost:443";

        // 🔹 Autenticación y obtención del token
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{ \"username\": \"admin\", \"password\": \"adminpass\" }")
                .when()
                .post("/api/auth/login");

        response.then().log().all(); // Imprimir respuesta para depuración

        authToken = response.getCookie("AuthToken"); // Obtener token de la cookie
        Assertions.assertNotNull(authToken, "Error: No se obtuvo un token JWT");
    }

    @Test
    @Order(1)
    public void testGetAllResults() {
        Response response = given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .get("/api/results");

        // 🔹 Log completo de la respuesta
        response.then().log().all();

        // 🔹 Captura la respuesta en un String y la imprime
        String jsonResponse = response.getBody().asString();
        System.out.println("🔹 Respuesta obtenida: " + jsonResponse);

        // 🔹 Verifica que el código de estado sea 200
        response.then()
                .statusCode(200)
                .body("content", notNullValue()) // Se asegura que 'content' exista en la respuesta
                .body("content.size()", greaterThanOrEqualTo(0));
    }


    @Test
    @Order(2)
    public void testCreateResult() {
        String newResultJson = """
        {
            \"value\": 12.5,
            \"athlete\": { \"licenseNumber\": \"M12345\" },
            \"discipline\": { \"id\": 1 },
            \"event\": { \"id\": 1 }
        }
        """;

        Response response = given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(newResultJson)
                .when()
                .post("/api/results");

        response.then().log().all();

        resultId = response.then()
                .statusCode(201)
                .body("value", equalTo(12.5F))
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    @Test
    @Order(3)
    public void testUpdateResult() {
        String updatedResultJson = """
        {
            \"value\": 12.25,
            \"athlete\": { \"licenseNumber\": \"M12345\" },
            \"discipline\": { \"id\": 1 },
            \"event\": { \"id\": 1 }
        }
        """;

        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(updatedResultJson)
                .when()
                .put("/api/results/{id}", resultId)
                .then()
                .log().all()
                .statusCode(200)
                .body("value", equalTo(12.25F))
                .body("id", notNullValue());
    }

    @Test
    @Order(4)
    public void testDeleteResult() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .delete("/api/results/{id}", resultId)
                .then()
                .log().all()
                .statusCode(anyOf(is(200), is(204)));
    }
}
