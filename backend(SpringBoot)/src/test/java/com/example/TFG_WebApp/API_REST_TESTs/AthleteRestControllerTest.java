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
public class AthleteRestControllerTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://localhost";
        RestAssured.port = 443;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    @Order(1)
    public void testGetAllAthletes() {
        given()
                .when()
                .get("/api/athletes")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThan(0));
    }

    @Test
    @Order(2)
    public void testCreateAthlete() {
        String newAthleteJson = """
        {
           \"licenseNumber\": \"A98764\",
           \"firstName\": \"Daniel\",
           \"lastName\": \"Villaseñor\",
           \"birthDate\": \"1996-12-01\",
           \"coach\": { \"licenseNumber\": \"C54321\" }
        }
        """;

        given()
                .header("Content-Type", "application/json")
                .body(newAthleteJson)
                .when()
                .post("/api/athletes")
                .then()
                .statusCode(201)
                .body("licenseNumber", equalTo("A98764"))
                .body("firstName", equalTo("Daniel"));
    }

    @Test
    @Order(3)
    public void testUpdateAthlete() {
        String updatedAthleteJson = """
        {
            \"licenseNumber\": \"A98764\",
            \"firstName\": \"Miguel\",
            \"lastName\": \"Fernandez\",
            \"birthDate\": \"1996-12-01\",
            \"coach\": { \"licenseNumber\": \"C54321\" }
        }
        """;

        given()
                .header("Content-Type", "application/json")
                .body(updatedAthleteJson)
                .when()
                .put("/api/athletes/A98764")
                .then()
                .statusCode(200)
                .body("licenseNumber", equalTo("A98764"))
                .body("firstName", equalTo("Miguel"));
    }

    @Test
    @Order(4)
    public void testDeleteAthlete() {
        given()
                .when()
                .delete("/api/athletes/A98764")
                .then()
                .statusCode(204);
    }
}