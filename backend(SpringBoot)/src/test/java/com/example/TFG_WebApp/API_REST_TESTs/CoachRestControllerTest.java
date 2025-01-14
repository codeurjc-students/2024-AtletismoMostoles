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
public class CoachRestControllerTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://localhost";
        RestAssured.port = 443;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    @Order(1)
    public void testGetAllCoaches() {
        given()
                .when()
                .get("/api/coaches")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThan(0));
    }

    @Test
    @Order(2)
    public void testCreateCoach() {
        String newCoachJson = """
    {
      \"licenseNumber\": \"F38455\",
      \"firstName\": \"Pilar\",
      \"lastName\": \"Torres\"
    }
    """;

     given()
             .header("Content-Type", "application/json")
             .body(newCoachJson)
             .when()
             .post("/api/coaches")
             .then()
             .statusCode(201)
             .body("licenseNumber", equalTo("F38455"))
             .body("firstName", equalTo("Pilar"))
             .body("lastName", equalTo("Torres"));
    }


    @Test
    @Order(3)
    public void testUpdateCoach() {
        String updatedCoachJson = """
        {
           \"licenseNumber\": \"F38455\",
           \"firstName\": \"Pilar\",
           \"lastName\": \"Martin\"
        }
        """;

        given()
                .header("Content-Type", "application/json")
                .body(updatedCoachJson)
                .when()
                .put("/api/coaches/F38455")
                .then()
                .statusCode(200)
                .body("licenseNumber", equalTo("F38455"))
                .body("firstName", equalTo("Pilar"))
                .body("lastName", equalTo("Martin"));
    }

    @Test
    @Order(4)
    public void testDeleteCoach() {
        given()
                .when()
                .delete("/api/coaches/F38455")
                .then()
                .statusCode(204);
    }
}
