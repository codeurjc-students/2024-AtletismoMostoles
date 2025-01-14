package com.example.TFG_WebApp.API_REST_TESTs;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


@TestMethodOrder(OrderAnnotation.class)
public class DisciplineRestControllerTest {

    private static int disciplineId;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://localhost";
        RestAssured.port = 443;
        RestAssured.useRelaxedHTTPSValidation();

    }

    @Test
    @Order(1)
    public void testGetAllDisciplines() {
        given()
                .when()
                .get("/api/disciplines")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThan(0));
    }

    @Test
    @Order(2)
    public void testCreateCoach() {
        String newCoachJson = """
    {
        \"name\": \"Lanzamiento de Martillo\",
        \"description\": \"Jueves 19:30 - 21:30 y Viernes 19:30 - 21:30\"
    }
    """;

        disciplineId = given()
                .header("Content-Type", "application/json")
                .body(newCoachJson)
                .when()
                .post("/api/disciplines")
                .then()
                .statusCode(201)
                .body("name", equalTo("Lanzamiento de Martillo"))
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    @Test
    @Order(3)
    public void testUpdateDiscipline() {
        String updatedDisciplineJson = """
        {
            \"name\": \"Lanzamiento de Disco\",
            \"description\": \"Jueves 19:30 - 21:30 y Viernes 19:30 - 21:30\"
        }
        """;

        given()
                .header("Content-Type", "application/json")
                .body(updatedDisciplineJson)
                .when()
                .put("/api/disciplines/{id}", disciplineId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Lanzamiento de Disco"));
    }

    @Test
    @Order(4)
    public void testDeleteDiscipline() {
        given()
                .when()
                .delete("/api/disciplines/{id}", disciplineId)
                .then()
                .statusCode(204);
    }
}
