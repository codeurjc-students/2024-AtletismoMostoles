package com.example.service1.API_REST_TESTs;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DisciplineRestControllerTest {

    private static int disciplineId;
    private static String authToken;

    @BeforeAll
    static void setup() {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = "https://localhost:443";

        Response response = given()
                .contentType(ContentType.JSON)
                .body("{ \"username\": \"admin\", \"password\": \"adminpass\" }")
                .when()
                .post("/api/auth/login");

        authToken = response.getCookie("AuthToken");
        Assertions.assertNotNull(authToken);
    }

    @Test
    void testGetAllDisciplines() {
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
    void testCreateDiscipline() {
        Response response = createDiscipline();

        response.then()
                .statusCode(201)
                .body("name", equalTo("Lanzamiento de Martillo"));

        Assertions.assertNotNull(disciplineId);
        deleteDiscipline();
    }

    @Test
    void testUpdateDiscipline() {
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
    void testDeleteDiscipline() {
        createDiscipline();
        deleteDiscipline()
                .then()
                .statusCode(anyOf(is(200), is(204)));
    }

    @Test
    void testGetDiscipline_NotFound() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .get("/api/disciplines/999999")
                .then()
                .statusCode(404);
    }

    @Test
    void testDeleteDiscipline_Unauthorized() {
        createDiscipline();

        given()
                .when()
                .delete("/api/disciplines/{id}", disciplineId)
                .then()
                .statusCode(401);

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
        disciplineId = response.then().extract().path("id");
        return response;
    }

    private static Response deleteDiscipline() {
        return given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .delete("/api/disciplines/{id}", disciplineId);
    }
}
