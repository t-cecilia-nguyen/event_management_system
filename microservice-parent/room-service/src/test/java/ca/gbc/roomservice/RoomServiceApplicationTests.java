package ca.gbc.roomservice;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import static org.flywaydb.core.internal.util.CollectionsUtils.hasItems;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoomServiceApplicationTests {
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    static {
        postgreSQLContainer.start();
    }

    @Test
    void createRoomTest() {
        String requestBody = """
                {
                    "roomName": "Lecture Hall D",
                    "capacity": "200",
                    "features": "Projector, Whiteboard, Sound System",
                    "availability": "true"
                }
                """;

        // Create room and log response
        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/rooms")
                .then()
                .log().all()
                .statusCode(201)
                .body("roomName", Matchers.equalTo("Lecture Hall D"))
                .body("capacity", Matchers.equalTo(200))
                .body("features", Matchers.equalTo("Projector, Whiteboard, Sound System"))
                .body("availability", Matchers.equalTo(true));
    }

    @Test
    void getAllRoomsTest() {
        // Seed data
        String[][] seedRooms = {
                {"Lecture Hall A", "150", "Projector, Whiteboard, Sound System", "true"},
                {"Conference Room B", "50", "Video Conferencing, Whiteboard, Coffee Machine", "false"},
                {"Workshop Room C", "30", "Interactive Display, Whiteboard, Sound System", "true"}
        };

        // Log response once
        RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/rooms")
                .then()
                .log().all()
                .statusCode(200)
                .extract();

        // Verify each room detail
        for (int i = 0; i < seedRooms.length; i++) {
            RestAssured.given()
                    .contentType("application/json")
                    .when()
                    .get("/rooms")
                    .then()
                    .body("[" + i + "].roomName", Matchers.equalTo(seedRooms[i][0]))
                    .body("[" + i + "].capacity", Matchers.equalTo(Integer.parseInt(seedRooms[i][1])))
                    .body("[" + i + "].features", Matchers.equalTo(seedRooms[i][2]))
                    .body("[" + i + "].availability", Matchers.equalTo(Boolean.parseBoolean(seedRooms[i][3])));
        }
    }

    @Test
    void getRoomByIdTest() {
        // Using Seed Data. Verify:
        // ('Conference Room B', 50, 'Video Conferencing, Whiteboard, Coffee Machine', false) -- ID 2

        // Specify room ID to verify
        int roomIDToVerify = 2;

        // Verify Room by ID and log response
        RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/rooms/" + roomIDToVerify)
                .then()
                .log().all()
                .statusCode(200)
                .body("roomName", Matchers.equalTo("Conference Room B"))
                .body("capacity", Matchers.equalTo(50))
                .body("features", Matchers.equalTo("Video Conferencing, Whiteboard, Coffee Machine"))
                .body("availability", Matchers.equalTo(false));
    }

    @Test
    void updateRoomTest() {
        // Seed Data: Update room with ID 1. ('Lecture Hall A', 150, 'Projector, Whiteboard, Sound System', true)

        int roomId = 1;

        // Room ID 1, updated information
        String updateRequestBody = """
                {
                    "roomName": "Lecture Hall B",
                    "capacity": "100",
                    "features": "Projector, Whiteboard",
                    "availability": "false"
                }
                """;

        // Update
        RestAssured.given()
                .contentType("application/json")
                .body(updateRequestBody)
                .when()
                .put("/rooms/" + roomId)
                .then()
                .log().all()
                .statusCode(204);

        // Verify update and log response
        RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/rooms/" + roomId)
                .then()
                .log().all()
                .statusCode(200)
                .body("roomName", Matchers.equalTo("Lecture Hall B"))
                .body("capacity", Matchers.equalTo(100))
                .body("features", Matchers.equalTo("Projector, Whiteboard"))
                .body("availability", Matchers.equalTo(false));
    }

    @Test
    void deleteRoomTest() {
        String requestBody = """
                {
                    "roomName": "Lecture Hall B",
                    "capacity": "100",
                    "features": "Projector, Whiteboard",
                    "availability": "false"
                }
                """;

        // Create room and retrieve ID
        Integer roomId = RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/rooms")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .path("id");

        // Delete room
        RestAssured.given()
                .contentType("application/json")
                .when()
                .delete("/rooms/" + roomId)
                .then()
                .log().all()
                .statusCode(204);

        // Verify deletion and log response
        RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/rooms/" + roomId)
                .then()
                .log().all()
                .statusCode(404);
    }

    @Test
    void checkAllAvailabilityTest() {
        String[][] rooms = {
                {"Lecture Hall A", "200", "Projector, Whiteboard, Sound System", "true"},
                {"Conference Room B", "50", "Projector, Whiteboard", "true"},
                {"Classroom C", "50", "Projector", "false"}
        };

        // Create 3 new rooms
        for (String[] room : rooms) {
            String requestBody = String.format("""
                    {
                        "roomName": "%s",
                        "capacity": "%s",
                        "features": "%s",
                        "availability": "%s"
                    }
                    """, room[0], room[1], room[2], room[3]);

            RestAssured.given()
                    .contentType("application/json")
                    .body(requestBody)
                    .when()
                    .post("/rooms")
                    .then()
                    .log().all()
                    .statusCode(201);
        }

        // Verify with true parameter and log response
        RestAssured.given()
                .contentType("application/json")
                .param("availability", "true")
                .when()
                .get("/rooms/availability")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    void checkRoomAvailabilityTest() {
        // Room ID 2 = seed data

        // Verify
        RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/rooms/2/availability")
                .then()
                .log().all()
                .statusCode(200)
                .body(Matchers.equalTo("false"));

        // With new test room
        String requestBody = """
            {
                "roomName": "Study Room G",
                "capacity": "100",
                "features": "Projector, Whiteboard",
                "availability": "true"
            }
            """;

        // Create new room
        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/rooms")
                .then()
                .log().all()
                .statusCode(201);

        // Verify and log response
        RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/rooms/4/availability") // new room is ID 4 because seed data has 3 rooms
                .then()
                .log().all()
                .statusCode(200)
                .body(Matchers.equalTo("true"));
    }
}
