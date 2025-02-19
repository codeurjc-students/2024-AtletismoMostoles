package com.example.TFG_WebApp.API_REST_TESTs;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DisciplineRestControllerTest {

    private static int disciplineId;
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
    public void testGetAllDisciplines() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .get("/api/disciplines")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void testCreateDiscipline() {
        Response response = createDiscipline();

        response.then().log().all();

        response.then()
                .statusCode(201)
                .body("name", equalTo("Lanzamiento de Martillo"));

        Assertions.assertNotNull(disciplineId, "Error: No se obtuvo un ID para la nueva disciplina");
        deleteDiscipline();
    }

    private static Response createDiscipline() {
        String newDisciplineJson = """
        {
            \"name\": \"Lanzamiento de Martillo\",
            \"description\": \"Jueves 19:30 - 21:30 y Viernes 19:30 - 21:30\"
        }
        """;

        Response response = given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(newDisciplineJson)
                .when()
                .post("/api/disciplines");
        disciplineId = response
                .then()
                .body("name", equalTo("Lanzamiento de Martillo"))
                .extract().path("id");

        return response;
    }

    @Test
    public void testUpdateDiscipline() {
        createDiscipline();
        String updatedDisciplineJson = """
        {
            \"name\": \"Lanzamiento de Disco\",
            \"description\": \"Jueves 19:30 - 21:30 y Viernes 19:30 - 21:30\"
        }
        """;

        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(updatedDisciplineJson)
                .when()
                .put("/api/disciplines/{id}", disciplineId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Lanzamiento de Disco"));
        deleteDiscipline();
    }

    @Test
    @Order(4)
    public void testDeleteDiscipline() {
        createDiscipline();
        deleteDiscipline()
                .then()
                .statusCode(anyOf(is(200), is(204)));
    }

    private static Response deleteDiscipline() {
        return given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .delete("/api/disciplines/{id}", disciplineId);
    }
}
