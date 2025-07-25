package com.example.service1.API_REST_TESTs;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AthleteRestControllerTest {

    private static String authToken;

    @BeforeAll
    static void setup() {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = "https://localhost:9091";

        Response response = given()
                .contentType(ContentType.JSON)
                .body("{ \"username\": \"admin\", \"password\": \"adminpass\" }")
                .when()
                .post("/api/auth/login");

        authToken = response.getCookie("AuthToken");
        Assertions.assertNotNull(authToken, "Auth token should not be null after login.");
    }

    @Test
    void testGetAllAthletes() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .get("/api/athletes")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThanOrEqualTo(0));
    }

    @Test
    void testGetAthlete_NotFound() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .get("/api/athletes/UNKNOWN999")
                .then()
                .statusCode(404);
    }

    @Test
    void testCreateAthlete_InvalidData() {
        String invalidAthlete = """
            {
                "licenseNumber": "",
                "firstName": ""
            }
        """;

        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(invalidAthlete)
                .when()
                .post("/api/athletes")
                .then()
                .statusCode(anyOf(is(400), is(409))); // Puede ser validación o duplicidad
    }

    @Test
    void testCreateUpdateAndDeleteAthlete() {
        // Crear atleta
        createAthlete().then()
                .statusCode(anyOf(is(200), is(201)))
                .body("firstName", equalTo("Daniel"))
                .body("licenseNumber", equalTo("A98764"));

        // Actualizar atleta
        String updatedAthlete = """
            {
                "licenseNumber": "A98764",
                "firstName": "DanielUpdated",
                "lastName": "VillaseñorUpdated",
                "birthDate": "1996-12-01",
                "coach": { "licenseNumber": "1001" },
                "disciplines": [
                    { "id": 1 }
                ]
            }
        """;

        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(updatedAthlete)
                .when()
                .put("/api/athletes/A98764")
                .then()
                .statusCode(200)
                .body("firstName", equalTo("DanielUpdated"));

        // Borrar atleta
        deleteAthlete().then()
                .statusCode(204);
    }

    @Test
    void testFilterAthletes() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .queryParam("firstName", "Daniel")
                .queryParam("page", 0)
                .queryParam("size", 5)
                .when()
                .get("/api/athletes/filter")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThanOrEqualTo(0));
    }

    @Test
    void testDeleteAthlete_Unauthorized() {
        // Crear atleta
        createAthlete().then()
                .statusCode(anyOf(is(200), is(201)));

        // Intentar borrar sin autorización
        given()
                .when()
                .delete("/api/athletes/A98764")
                .then()
                .statusCode(401);

        // Borrar con autorización
        deleteAthlete().then()
                .statusCode(204);
    }

    // ---------- HELPERS ----------

    private static Response createAthlete() {
        String newAthlete = """
            {
                "licenseNumber": "A98764",
                "firstName": "Daniel",
                "lastName": "Villaseñor",
                "birthDate": "1996-12-01",
                "coach": { "licenseNumber": "1001" },
                "disciplines": [
                    { "id": 1 }
                ]
            }
        """;

        return given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(newAthlete)
                .when()
                .post("/api/athletes");
    }

    private static Response deleteAthlete() {
        return given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .delete("/api/athletes/A98764");
    }
}