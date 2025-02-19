package com.example.TFG_WebApp.API_REST_TESTs;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EquipmentRestControllerTest {

    private static int equipmentId;
    private static String authToken;  // 🔹 Token de autenticación

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://localhost";
        RestAssured.port = 443;
        RestAssured.useRelaxedHTTPSValidation();

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
    public void testGetAllEquipment() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .get("/api/equipment")
                .then()
                .log().all()
                .statusCode(200)
                .body("content.size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void testCreateEquipment() {
        Response response = createEquipment();

        response.then().log().all();

        response.then()
                .statusCode(201)
                .body("name", equalTo("Martillo"))
                .body("id", notNullValue());

        deleteEquipment();
    }

    private static Response createEquipment() {
        String newEquipmentJson = """
        {
            \"name\": \"Martillo\",
            \"description\": \"4kg, de metal\"
        }
        """;

        Response response = given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(newEquipmentJson)
                .when()
                .post("/api/equipment");

        equipmentId = response.then()
                .body("name", equalTo("Martillo"))
                .body("id", notNullValue())
                .extract()
                .path("id");

        return response;
    }

    @Test
    public void testUpdateEquipment() {
        createEquipment();
        String updatedEquipmentJson = """
        {
           \"name\": \"Martillo\",
           \"description\": \"8kg, de metal\"
        }
        """;

        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(updatedEquipmentJson)
                .when()
                .put("/api/equipment/{id}", equipmentId)
                .then()
                .log().all()
                .statusCode(200)
                .body("name", equalTo("Martillo"))
                .body("id", equalTo(equipmentId))
                .body("description", equalTo("8kg, de metal"));
        deleteEquipment();
    }

    @Test
    public void testDeleteEquipment() {
        createEquipment();
        deleteEquipment()
                .then()
                .log().all()
                .statusCode(anyOf(is(200), is(204)));
    }

    private static Response deleteEquipment() {
        return given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .delete("/api/equipment/{id}", equipmentId);
    }
}
