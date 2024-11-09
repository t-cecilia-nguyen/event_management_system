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

import java.rmi.MarshalException;


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

    @Test
    void createUserTest() {
        String requestBody = """
                {
                    "name" : "Trang Nguyen",
                    "email" : "trang.nguyen3@georgebrown.ca",
                    "role" : "STUDENT",
                    "userType" : "STUDENT"
                }
                """;

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/users")
                .then()
                .log().all()
                .statusCode(201)
                .body("name", Matchers.equalTo("Trang Nguyen"))
                .body("email", Matchers.equalTo("trang.nguyen3@georgebrown.ca"))
                .body("role", Matchers.equalTo("STUDENT"))
                .body("userType", Matchers.equalTo("STUDENT"));
    }

    @Test
    void getAllUsersTest() {
        // Seed data
        String[][] seedUsers = {
                {"Trang Nguyen", "trang.nguyen3@georgebrown.ca", "STUDENT", "STUDENT"},
                {"Adam Simcoe", "adam.simcoe@georgebrown.ca", "PROFESSOR", "FACULTY"},
                {"Nhan Tran", "nhan.tran@georgebrown.ca", "ADMINISTRATOR", "STAFF"},
                {"Nhu Ly", "huynhyennhu.ly@georgebrown.ca", "TECHNICIAN", "STAFF"}
        };

        // Log response once
        RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/users")
                .then()
                .log().all()
                .statusCode(200)
                .extract();

        // Verify each user detail
        for (int i = 0; i < seedUsers.length; i++) {
            RestAssured.given()
                    .contentType("application/json")
                    .when()
                    .get("/users")
                    .then()
                    .body("[" + i + "].name", Matchers.equalTo(seedUsers[i][0]))
                    .body("[" + i + "].email", Matchers.equalTo(seedUsers[i][1]))
                    .body("[" + i + "].role", Matchers.equalTo(seedUsers[i][2]))
                    .body("[" + i + "].userType", Matchers.equalTo(seedUsers[i][3]));
        }
    }

    @Test
    void getUsersByIdTest() {
        // Using seed data to verify:
        // {"Nhu Ly", "huynhyennhu.ly@georgebrown.ca", "TECHNICIAN", "STAFF"} -- ID 4

        // Specify user ID to verify
        int userIdToVerify = 4;

        RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/users/" + userIdToVerify)
                .then()
                .log().all()
                .statusCode(200)
                .body("name", Matchers.equalTo("Nhu Ly"))
                .body("email", Matchers.equalTo("huynhyennhu.ly@georgebrown.ca"))
                .body("role", Matchers.equalTo("TECHNICIAN"))
                .body("userType", Matchers.equalTo("STAFF"));
    }

    @Test
    void updateUserTest() {
        // Using seed data to update user with ID 3.
        // {"Nhan Tran", "nhan.tran@georgebrown.ca", "ADMINISTRATOR", "STAFF"}

        int userId = 3;

        // Update information
        String updateRequestBody = """
                {
                    "name": "Nhan Tran",
                    "email": "nhan.tran@georgebrown.ca",
                    "role": "PROFESSOR",
                    "userType": "FACULTY"
                }
                """;

        // Update
        RestAssured.given()
                .contentType("application/json")
                .body(updateRequestBody)
                .when()
                .put("/users/" + userId)
                .then()
                .log().all()
                .statusCode(204);

        // Verify update and log response
        RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/users/" + userId)
                .then()
                .log().all()
                .statusCode(200)
                .body("name", Matchers.equalTo("Nhan Tran"))
                .body("email", Matchers.equalTo("nhan.tran@georgebrown.ca"))
                .body("role", Matchers.equalTo("PROFESSOR"))
                .body("userType", Matchers.equalTo("FACULTY"));
    }

    @Test
    void deleteUserTest() {
        // Deleting seed data with user ID = 2.
        int roomId = 2;

        RestAssured.given()
                .contentType("application/json")
                .when()
                .delete("/users/" + roomId)
                .then()
                .log().all()
                .statusCode(204);

        // Verify deletion and log response
        RestAssured.given()
                .contentType("application/json")
                .when()
                .get("/users/" + roomId)
                .then()
                .log().all()
                .statusCode(404);
    }

    @Test
    void checkAllUserTypeTest () {
        // Using Seed Data, check all UserType = STAFF

        // Verify with "STAFF" parameter and log response
        RestAssured.given()
                .contentType("application/json")
                .param("userType", "STAFF")
                .when()
                .get("/users/usertype")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    void checkUserTypeTest() {
        // Using seed data, User ID = 1

        // Verify
        RestAssured.given()
                .contentType("applicaiton/json")
                .when()
                .get("/users/1/usertype")
                .then()
                .log().all()
                .statusCode(200)
                .body(Matchers.equalTo("STUDENT"));
    }

}
