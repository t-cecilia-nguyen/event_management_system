package ca.gbc.roomservice;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.ArrayList;
import java.util.List;

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
                    "roomName": "Lecture Hall A",
                    "capacity": "200",
                    "features": "Projector, Whiteboard, Sound System",
                    "availability": "true"
                }                              
                """;

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/rooms")
                .then()
                .log().all()
                .statusCode(201)
                .body("roomName", Matchers.equalTo("Lecture Hall E"))
                .body("capacity", Matchers.equalTo(200))
                .body("features", Matchers.equalTo("Projector, Whiteboard, Sound System"))
                .body("availability", Matchers.equalTo(true));
    }

    @Test
    void getAllRoomsTest() {
        // Array of all rooms
        String[][] rooms = {
                {"Lecture Hall A", "200","Projector, Whiteboard, Sound System", "true"},
                {"Conference Room B", "50", "Projector, Whiteboard", "true"}
        };

        // Loop through array and create each room
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
                    .statusCode(201)
                    .body("roomName", Matchers.equalTo(room[0]))
                    .body("capacity", Matchers.equalTo(Integer.parseInt(room[1])))
                    .body("features", Matchers.equalTo(room[2]))
                    .body("availability", Matchers.equalTo(Boolean.parseBoolean(room[3])));
        }

        // Verify
        RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/rooms")
                .then()
                .log().all()
                .statusCode(200)
                .body("size()", Matchers.equalTo(rooms.length));

        for (int i = 0; i < rooms.length; i++) {
            RestAssured.given()
                    .contentType("application/json")
                    .when()
                    .get("/rooms")
                    .then()
                    .body("[" + i + "].roomName", Matchers.equalTo(rooms[i][0]))
                    .body("[" + i + "].capacity", Matchers.equalTo(Integer.parseInt(rooms[i][1])))
                    .body("[" + i + "].features", Matchers.equalTo(rooms[i][2]))
                    .body("[" + i + "].availability", Matchers.equalTo(Boolean.parseBoolean(rooms[i][3])));
        }
    }

    @Test
    void getRoomByIdTest() {
        String[][] rooms = {
                {"Lecture Hall A", "200","Projector, Whiteboard, Sound System", "true"},
                {"Conference Room B", "50", "Projector, Whiteboard", "true"},
                {"Classroom C", "50", "Projector", "false" }
        };

        // List to store room IDs
        List<Integer> roomIds = new ArrayList<>();

        // Create each room
        for (String[] room : rooms) {
            String requestBody = String.format("""
                    {
                        "roomName": "%s",
                        "capacity": "%s",
                        "features": "%s",
                        "availability": "%s"
                    }
                    """, room[0], room[1], room[2], room[3]);

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

            roomIds.add(roomId);
        }

        // Specify room ID to verify
        int roomIDToVerify = roomIds.get(2); // Third room

        String verifyBody = """
                {
                    "roomName": "Classroom C",
                    "capacity": "50",
                    "features": "Projector",
                    "availability": "false"
                }
                """;

        // Verify Room by ID
        RestAssured.given()
                .contentType("application/json")
                .body(verifyBody)
                .when()
                .get("/rooms/" + roomIDToVerify)
                .then()
                .log().all()
                .statusCode(200)
                .body("roomName", Matchers.equalTo("Classroom C"))
                .body("capacity", Matchers.equalTo(50))
                .body("features", Matchers.equalTo("Projector"))
                .body("availability", Matchers.equalTo(false));
    }

    @Test
    void updateRoomTest() {
        String requestBody = """
                {
                    "roomName": "Lecture Hall A",
                    "capacity": "200",
                    "features": "Projector, Whiteboard, Sound System",
                    "availability": "true"
                }                              
                """;

        // Retrieve room ID
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

        // Update room
        String updateRequestBody = """
                {
                    "roomName": "Lecture Hall B",
                    "capacity": "100",
                    "features": "Projector, Whiteboard",
                    "availability": "false"
                }
                """;

        RestAssured.given()
                .contentType("application/json")
                .body(updateRequestBody)
                .when()
                .put("/rooms/" + roomId)
                .then()
                .log().all()
                .statusCode(204);

        // Verify update
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

        // Verify deletion
        RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/rooms/" + roomId)
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    void checkAllAvailabilityTest() {
        String[][] rooms = {
                {"Lecture Hall A", "200", "Projector, Whiteboard, Sound System", "true"},
                {"Conference Room B", "50", "Projector, Whiteboard", "true"},
                {"Classroom C", "50", "Projector", "false"}
        };

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
        // Using seed data

        // Verify
        RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/rooms/2/availability")
                .then()
                .log().all()
                .statusCode(200)
                .body(Matchers.equalTo("false"));
    }
}
