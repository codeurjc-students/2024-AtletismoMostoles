package com.example.TFG_WebApp.API_REST_TESTs;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EventRestControllerTest {

    private static String authToken;
    private static int eventId;

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
    public void testGetAllEvents() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .get("/api/events")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void testCreateEvent() {
        Response response = createEvent();

        response.then().log().all();

        response.then()
                .statusCode(201)
                .body("name", equalTo("Ilumina Mostoles"))
                .body("date", equalTo("2025-10-04"));

        Assertions.assertNotNull(eventId, "Error: No se obtuvo un ID de evento");
        deleteEvent();
    }

    private static Response createEvent() {
        String newEventJson = """
        {
            \"name\": \"Ilumina Mostoles\",
            \"date\": \"2025-10-04\",
            \"isOrganizedByClub\": true
        }
        """;

        Response response = given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(newEventJson)
                .when()
                .post("/api/events");

        eventId = response.then()
                .body("name", equalTo("Ilumina Mostoles"))
                .body("date", equalTo("2025-10-04"))
                .extract()
                .path("id");

        return response;
    }

    @Test
    @Order(3)
    public void testUpdateEvent() {
        createEvent();
        String updatedEventJson = """
        {
            \"name\": \"Ilumina Mostoles\",
            \"date\": \"2025-12-04\",
            \"isOrganizedByClub\": true
        }
        """;

        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(updatedEventJson)
                .when()
                .put("/api/events/{id}", eventId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Ilumina Mostoles"))
                .body("date", equalTo("2025-12-04"));
        deleteEvent();
    }

    @Test
    @Order(4)
    public void testDeleteEvent() {
        createEvent();
        deleteEvent()
                .then()
                .statusCode(anyOf(is(200), is(204)));
    }

    private static Response deleteEvent() {
        return given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .delete("/api/events/{id}", eventId);
    }
}
