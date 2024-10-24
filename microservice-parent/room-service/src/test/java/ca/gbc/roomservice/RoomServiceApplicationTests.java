package ca.gbc.roomservice;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;

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
}
