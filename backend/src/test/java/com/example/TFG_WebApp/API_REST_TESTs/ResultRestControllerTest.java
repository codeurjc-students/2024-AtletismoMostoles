package com.example.TFG_WebApp.API_REST_TESTs;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ResultRestControllerTest {

    private static int resultId;
    private static String authToken;

    @BeforeAll
    static void setup() {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = "https://localhost:443";

        Response response = given()
                .contentType(ContentType.JSON)
                .body("{ \"username\": \"admin\", \"password\": \"adminpass\" }")
                .when()
                .post("/api/auth/login");

        authToken = response.getCookie("AuthToken");
        Assertions.assertNotNull(authToken, "Error: No se obtuvo un token JWT");
    }

    @Test
    void testGetAllResults() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .get("/api/results")
                .then()
                .statusCode(200)
                .body("content", notNullValue())
                .body("content.size()", greaterThanOrEqualTo(0));
    }

    @Test
    void testCreateResult() {
        Response response = createResult();

        response.then()
                .statusCode(201)
                .body("value", equalTo(12.5F))
                .body("id", notNullValue());

        deleteResult();
    }

    private static Response createResult() {
        String newResultJson = """
        {
            "value": 12.5,
            "athlete": { "licenseNumber": "A2001" },
            "discipline": { "id": 1 },
            "event": { "id": 1 }
        }
        """;

        Response response = given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(newResultJson)
                .when()
                .post("/api/results");

        resultId = response.then()
                .body("value", equalTo(12.5F))
                .body("id", notNullValue())
                .extract()
                .path("id");

        return response;
    }

    @Test
    void testUpdateResult() {
        createResult();
        String updatedResultJson = """
        {
            "value": 12.25,
            "athlete": { "licenseNumber": "A2001" },
            "discipline": { "id": 1 },
            "event": { "id": 1 }
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
                .statusCode(200)
                .body("value", equalTo(12.25F))
                .body("id", notNullValue());

        deleteResult();
    }

    @Test
    @Order(4)
    void testDeleteResult() {
        createResult();
        deleteResult()
                .then()
                .statusCode(anyOf(is(200), is(204)));
    }

    private static Response deleteResult() {
        return given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .delete("/api/results/{id}", resultId);
    }

    @Test
    void testCreateMultipleResults() {
        String multipleResultsJson = """
        [
            {
                "value": 13.0,
                "athlete": { "licenseNumber": "A2001" },
                "discipline": { "id": 1 },
                "event": { "id": 1 }
            },
            {
                "value": 11.75,
                "athlete": { "licenseNumber": "A2002" },
                "discipline": { "id": 1 },
                "event": { "id": 1 }
            }
        ]
        """;

        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(multipleResultsJson)
                .when()
                .post("/api/results/batch")
                .then()
                .statusCode(201)
                .body("size()", greaterThanOrEqualTo(2))
                .body("[0].value", equalTo(13.0F))
                .body("[1].value", equalTo(11.75F));
    }

    @Test
    void testDeleteResult_Unauthorized() {
        createResult();
        given()
                .when()
                .delete("/api/results/{id}", resultId)
                .then()
                .statusCode(401); // O 403 según configuración
    }

}
