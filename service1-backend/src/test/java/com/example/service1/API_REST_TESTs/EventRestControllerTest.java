package com.example.service1.API_REST_TESTs;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EventRestControllerTest {

    private static String authToken;
    private static int eventId;

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
    void testGetAllEvents() {
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
    void testCreateEvent() {
        Response response = createEvent();

        response.then()
                .statusCode(200)
                .body("name", equalTo("Ilumina Mostoles"))
                .body("date", equalTo("2025-10-04"));

        Assertions.assertNotNull(eventId);
        deleteEvent();
    }

    @Test
    void testUpdateEvent() {
        createEvent();

        String updatedEventJson = """
        {
            "name": "Carrera Ilumina Mostoles",
                "date": "2025-11-04",
                "organizedByClub": true,
                "mapLink": "",
                "imageLink": "",
                "disciplineIds": [],
                "creationTime": "2025-07-14T15:00:00"
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
                .body("name", equalTo("Carrera Ilumina Mostoles"))
                .body("date", equalTo("2025-11-04"));

        deleteEvent();
    }

    @Test
    void testDeleteEvent() {
        createEvent();

        deleteEvent()
                .then()
                .statusCode(anyOf(is(200), is(204)));
    }

    @Test
    void testGetEvent_NotFound() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .get("/api/events/999999")
                .then()
                .statusCode(500);
    }

    @Test
    void testCreateEvent_InvalidData() {
        // Falta 'date' y 'name' está vacío => debería fallar con 400
        String invalidEventJson = """
        {
            \"name\": \"\",
            \"organizedByClub\": true
        }
        """;

        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(invalidEventJson)
                .when()
                .post("/api/events")
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    void testDeleteEvent_Unauthorized() {
        createEvent();

        given()
                .when()
                .delete("/api/events/{id}", eventId)
                .then()
                .statusCode(401);

        deleteEvent(); // Limpieza con credenciales válidas
    }

    @Test
    void testFilterEvents() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .queryParam("organizedByClub", true)
                .queryParam("startDate", "2025-01-01")
                .queryParam("endDate", "2025-12-31")
                .when()
                .get("/api/events")
                .then()
                .statusCode(200)
                .body("content.size()", greaterThanOrEqualTo(0));
    }

    private static Response createEvent() {
        String newEventJson = """
    {
        \"name\": \"Ilumina Mostoles\",
        \"date\": \"2025-10-04\",
        \"organizedByClub\": true,
        \"mapLink\": \"\",
        \"imageLink\": \"\",
        \"disciplineIds\": [],
        \"creationTime\": \"2025-07-14T15:00:00\"
    }
    """;

        Response response = given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .contentType(ContentType.JSON)
                .body(newEventJson)
                .when()
                .post("/api/events");

        response.then().statusCode(200);

        Integer id = response.then().extract().path("id");
        Assertions.assertNotNull(id);
        eventId = id;
        return response;
    }

    private static Response deleteEvent() {
        return given()
                .header("Authorization", "Bearer " + authToken)
                .cookie("AuthToken", authToken)
                .when()
                .delete("/api/events/{id}", eventId);
    }
}
