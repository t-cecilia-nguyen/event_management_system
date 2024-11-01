package ca.gbc.userservice;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceApplicationTests {

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

//    @Test
//    void createUserTest() {
//        String requestBody = """
//                {
//                    "name" : "Trang Nguyen",
//                    "email" : "trang.nguyen3@georgebrown.ca",
//                    "role" : "STUDENT",
//                    "userType" : "STUDENT"
//                }
//                """;
//
//        RestAssured.given()
//                .contentType("application/json")
//                .body(requestBody)
//                .when()
//                .post("/users")
//                .then()
//                .log().all()
//                .statusCode(201)
//                .body("name", Matchers.equalTo("Trang Nguyen"))
//                .body("email", Matchers.equalTo("trang.nguyen3@georgebrown.ca"))
//                .body("role", Matchers.equalTo("STUDENT"))
//                .body("userType", Matchers.equalTo("STUDENT"));
//    }

}
