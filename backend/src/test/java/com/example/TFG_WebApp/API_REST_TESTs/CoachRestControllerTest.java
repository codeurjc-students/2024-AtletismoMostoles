package com.example.TFG_WebApp.API_REST_TESTs;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CoachRestControllerTest {

    private static String authToken;

    @BeforeAll
    public static void setup() {
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
    public void testGetAllCoaches() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .get("/api/coaches")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void testCreateCoach() {
        Response response = createCoach();

        response.then()
                .statusCode(anyOf(is(200), is(201)))
                .body("licenseNumber", equalTo("F38455"))
                .body("firstName", equalTo("Pilar"))
                .body("lastName", equalTo("Torres"));

        deleteCoach();
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

        response.then()
                .statusCode(anyOf(is(200), is(204)));
    }

    @Test
    public void testGetCoach_NotFound() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .get("/api/coaches/UNKNOWN999")
                .then()
                .statusCode(404);
    }

    @Test
    public void testCreateCoach_InvalidData() {
        String invalidCoachJson = """
        {
            \"licenseNumber\": \"\",
            \"firstName\": \"\"
        }
        """;

        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(invalidCoachJson)
                .when()
                .post("/api/coaches")
                .then()
                .statusCode(400);
    }

    @Test
    public void testDeleteCoach_Unauthorized() {
        createCoach();

        given()
                .when()
                .delete("/api/coaches/F38455")
                .then()
                .statusCode(401);

        deleteCoach();
    }

    @Test
    public void testFilterCoaches() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .queryParam("firstName", "Pilar")
                .when()
                .get("/api/coaches/filter")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThanOrEqualTo(0));
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

        return given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(newCoachJson)
                .when()
                .post("/api/coaches");
    }

    private static Response deleteCoach() {
        return given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .delete("/api/coaches/F38455");
    }
}
