package com.example.service1.API_REST_TESTs;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ResultRestControllerTest {

    private static int resultId;
    private static String authToken;

    @BeforeAll
    static void setup() {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.baseURI = "https://service1-backend:9091";

        Response response = given()
                .contentType(ContentType.JSON)
                .body("{ \"username\": \"admin\", \"password\": \"adminpass\" }")
                .when()
                .post("/api/auth/login");

        authToken = response.getCookie("AuthToken");
        Assertions.assertNotNull(authToken, "Error: No se obtuvo un token JWT");
    }

    @Test
    void testGetAllResults() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .get("/api/results")
                .then()
                .statusCode(200)
                .body("content", notNullValue())
                .body("content.size()", greaterThanOrEqualTo(0));
    }

    @Test
    void testCreateResult() {
        Response response = createResult();

        response.then()
                .statusCode(200)
                .body("valor", equalTo("12.5"))
                .body("id", notNullValue());
    }

    private static Response createResult() {
        String newResultJson = """
        {
            "value": "12.5",
            "athleteId": "A2001",
            "disciplineId": 1,
            "eventId": 1
        }
        """;

        Response response = given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(newResultJson)
                .when()
                .post("/api/results");

        System.out.println("Respuesta createResult:\n" + response.getBody().asPrettyString());

        Integer idExtraido = response.then().extract().path("id");
        if (idExtraido == null) {
            throw new AssertionError("Error: el campo 'id' no está presente en la respuesta.");
        }

        resultId = idExtraido;
        return response;
    }

    @Test
    void testCreateMultipleResults() {
        String multipleResultsJson = """
        [
            {
                "value": "13.0",
                "athleteId": "A2001",
                "disciplineId": 1,
                "eventId": 1
            },
            {
                "value": "11.75",
                "athleteId": "A2002",
                "disciplineId": 1,
                "eventId": 1
            }
        ]
        """;

        Response response = given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(multipleResultsJson)
                .when()
                .post("/api/results/batch");

        System.out.println("Respuesta createMultiple:\n" + response.getBody().asPrettyString());

        response.then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(2))
                .body("[0].valor", equalTo("13.0"))
                .body("[1].valor", equalTo("11.75"));
    }

    @Test
    void testGetResultsByAthlete() {
        createResult();

        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .get("/api/results/athlete/A2001")
                .then()
                .statusCode(200)
                .body("content", notNullValue());
    }

    @Test
    void testGetResultsByEvent() {
        createResult();

        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .get("/api/results/event/1")
                .then()
                .statusCode(200)
                .body("content", notNullValue());
    }

    @Test
    void testGetPdfHistory() {
        createResult();

        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .get("/api/results/pdf/history/A2001")
                .then()
                .statusCode(200)
                .body("content", notNullValue());
    }

    @Test
    void testRequestPdfGeneration() {
        createResult();

        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .post("/api/results/pdf/A2001")
                .then()
                .statusCode(202);
    }

    @Test
    void testDeleteResult_Unauthorized() {
        createResult();

        given()
                .when()
                .delete("/api/results/{id}", resultId)
                .then()
                .statusCode(anyOf(is(401), is(403), is(405))); // depende de config
    }
}
