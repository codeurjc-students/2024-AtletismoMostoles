package com.example.TFG_WebApp.API_REST_TESTs;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AthleteRestControllerTest {

    private static String authToken;  // 🔹 Almacena el token de autenticación para futuras solicitudes

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
    public void testGetAllAthletes() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .get("/api/athletes")
                .then()
                .log().all()
                .statusCode(200)
                .body("content.size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void testCreateAthlete() {
        Response response = createAthlete();

        response.then().log().all();

        response.then()
                .statusCode(anyOf(is(200), is(201)))
                .body("firstName", equalTo("Daniel"))
                .body("licenseNumber", equalTo("A98764"));

        deleteAthlete();
    }

    private static Response createAthlete() {
        String newAthlete = """
            {
             \"licenseNumber\": \"A98764\",
             \"firstName\": \"Daniel\",
             \"lastName\": \"Villaseñor\",
             \"birthDate\": \"1996-12-01\",
             \"coach\": { \"licenseNumber\": \"C1001\"},
             \"disciplines\": [
                {\"id\": 1}
                ]
             }
        """;

        Response response = given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(newAthlete)
                .when()
                .post("/api/athletes");
        return response;
    }

    @Test
    public void testUpdateAthlete() {
        createAthlete();

        String updatedAthlete = """
            {
            \"licenseNumber\": \"A98764\",
            \"firstName\": \"Mateo\",
            \"lastName\": \"Martin\",
            \"birthDate\": \"1996-12-01\",
            \"coach\": { \"licenseNumber\": \"C1001\"},
            \"disciplines\": [
                {\"id\": 1}
            ]
            }
        """;

        Response response = given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(updatedAthlete)
                .when()
                .put("/api/athletes/A98764");

        response.then().log().all();

        response.then()
                .statusCode(200)
                .body("licenseNumber", equalTo("A98764"))
                .body("firstName", equalTo("Mateo"))
                .body("lastName", equalTo("Martin"));

        deleteAthlete();
    }

    @Test
    public void testDeleteAthlete() {
        createAthlete();
        Response response = deleteAthlete();

        response.then()
                .statusCode(anyOf(is(200), is(204)));
    }

    private static Response deleteAthlete() {
        Response response = given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .delete("/api/athletes/A98764");

        response.then().log().all();
        return response;
    }
}
