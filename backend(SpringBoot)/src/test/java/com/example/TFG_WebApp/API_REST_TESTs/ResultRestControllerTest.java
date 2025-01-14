package com.example.TFG_WebApp.API_REST_TESTs;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class ResultRestControllerTest {

    private static int resultId;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://localhost";
        RestAssured.port = 443;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    @Order(1)
    public void testGetAllResults() {
        given()
                .when()
                .get("/api/results")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThan(0));
    }

    @Test
    @Order(2)
    public void testCreateResult() {
        String newResultJson = """
        {
            \"value\": 12.5,
            \"athlete\": { \"licenseNumber\": \"A12345\" },
            \"discipline\": { \"id\": 1 },
            \"event\": { \"id\": 1 }
        }
        """;

        resultId = given()
                .header("Content-Type", "application/json")
                .body(newResultJson)
                .when()
                .post("/api/results")
                .then()
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
            \"athlete\": { \"licenseNumber\": \"A12345\" },
            \"discipline\": { \"id\": 1 },
            \"event\": { \"id\": 1 }
        }
        """;

        given()
                .header("Content-Type", "application/json")
                .body(updatedResultJson)
                .when()
                .put("/api/results/{id}", resultId)
                .then()
                .statusCode(200)
                .body("value", equalTo(12.25F))
                .body("id", notNullValue());
    }

    @Test
    @Order(4)
    public void testDeleteResult() {
        given()
                .when()
                .delete("/api/results/{id}", resultId)
                .then()
                .statusCode(204);
    }
}
