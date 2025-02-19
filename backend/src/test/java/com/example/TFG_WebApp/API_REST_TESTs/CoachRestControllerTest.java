package com.example.TFG_WebApp.API_REST_TESTs;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CoachRestControllerTest {

    private static String authToken;  // 🔹 Almacena el token de autenticación

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
    public void testGetAllCoaches() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .get("/api/coaches")
                .then()
                .log().all()
                .statusCode(200)
                .body("content.size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void testCreateCoach() {
        Response response = createCoach();

        response.then().log().all();

        response.then()
                .statusCode(anyOf(is(200), is(201)))
                .body("licenseNumber", equalTo("F38455"))
                .body("firstName", equalTo("Pilar"))
                .body("lastName", equalTo("Torres"));
        deleteCoach();
    }

    private static Response createCoach() {
        String newCoachJson = """
        {
          \"licenseNumber\": \"F38455\",
          \"firstName\": \"Pilar\",
          \"lastName\": \"Torres\",
          \"disciplines\":[
              {\"id\":1}
          ]
        }
        """;

        Response response = given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(newCoachJson)
                .when()
                .post("/api/coaches");
        return response;
    }

    @Test
    public void testUpdateCoach() {
        createCoach();
        String updatedCoachJson = """
        {
          \"licenseNumber\": \"F38455\",
          \"firstName\": \"Pilar\",
          \"lastName\": \"Martin\",
          \"disciplines\":[
              {\"id\":1}
          ]
        }
        """;

        Response response = given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(updatedCoachJson)
                .when()
                .put("/api/coaches/F38455");

        response.then().log().all();

        response.then()
                .statusCode(200)
                .body("licenseNumber", equalTo("F38455"))
                .body("firstName", equalTo("Pilar"))
                .body("lastName", equalTo("Martin"));
        deleteCoach();
    }

    @Test
    @Order(4)
    public void testDeleteCoach() {
        createCoach();

        Response response = deleteCoach();

        response.then().log().all();

        response.then()
                .statusCode(anyOf(is(200), is(204)));
    }

    private static Response deleteCoach() {
        Response response = given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .delete("/api/coaches/F38455");
        return response;
    }
}
