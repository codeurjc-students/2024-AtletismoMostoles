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
public class EquipmentRestControllerTest {

    private static int equipmentId;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://localhost";
        RestAssured.port = 443;
        RestAssured.useRelaxedHTTPSValidation();

    }

    @Test
    @Order(1)
    public void testGetAllEquipment() {
        given()
                .when()
                .get("/api/equipment")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThan(0));
    }

    @Test
    @Order(2)
    public void testCreateEquipment() {
        String newEquipmentJson = """
        {
            \"name\": \"Martillo\",
            \"description\": \"4kg, de metal\"
        }
        """;

        equipmentId = given()
                .header("Content-Type", "application/json")
                .body(newEquipmentJson)
                .when()
                .post("/api/equipment")
                .then()
                .statusCode(201)
                .body("name", equalTo("Martillo"))
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    @Test
    @Order(3)
    public void testUpdateEquipment() {
        String updatedEquipmentJson = """
        {
           \"name\": \"Martillo\",
           \"description\": \"8kg, de metal\"
        }
        """;

        given()
                .header("Content-Type", "application/json")
                .body(updatedEquipmentJson)
                .when()
                .put("/api/equipment/{id}", equipmentId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Martillo"))
                .body("id", equalTo(equipmentId))
                .body("description", equalTo("8kg, de metal"));
    }

    @Test
    @Order(4)
    public void testDeleteEquipment() {
        given()
                .when()
                .delete("/api/equipment/{id}", equipmentId)
                .then()
                .statusCode(204);
    }
}
