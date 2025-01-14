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
public class EventRestControllerTest {

    private static int eventId;
    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://localhost";
        RestAssured.port = 443;
        RestAssured.useRelaxedHTTPSValidation();

    }

    @Test
    @Order(1)
    public void testGetAllEvents() {
        given()
                .when()
                .get("/api/events")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThan(0));
    }

    @Test
    @Order(2)
    public void testCreateEvent() {
        String newEventJson = """
        {
            \"name\": \"Ilumina Mostoles\",
            \"date\": \"2025-10-04\",
            \"isOrganizedByClub\": true
        }
        """;

        eventId = given()
                .header("Content-Type", "application/json")
                .body(newEventJson)
                .when()
                .post("/api/events")
                .then()
                .statusCode(201)
                .body("name", equalTo("Ilumina Mostoles"))
                .body("date", equalTo("2025-10-04"))
                .extract()
                .path("id");
    }

    @Test
    @Order(3)
    public void testUpdateEvent() {
        String updatedEventJson = """
        {
            \"name\": \"Ilumina Mostoles\",
            \"date\": \"2025-12-04\",
            \"isOrganizedByClub\": true
        }
        """;

        given()
                .header("Content-Type", "application/json")
                .body(updatedEventJson)
                .when()
                .put("/api/events/{id}", eventId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Ilumina Mostoles"))
                .body("date", equalTo("2025-12-04"));
    }

    @Test
    @Order(4)
    public void testDeleteEvent() {
        given()
                .when()
                .delete("/api/events/{id}", eventId)
                .then()
                .statusCode(204);
    }
}
