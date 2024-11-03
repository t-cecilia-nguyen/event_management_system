package ca.gbc.eventservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.MongoDBContainer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = EventServiceApplication.class)
class EventServiceApplicationTests {
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @LocalServerPort
    private Integer port;


    @Autowired
    private TestRestTemplate restTemplate;



    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    static {
        mongoDBContainer.start();
    }



    @Test
    public void testCreateEvent() {
        long userId = 1;
        String endpoint = "/api/event" + "?userId=" + userId;

        String eventRequest = """
       {
            "eventName": "Sample Event",
            "eventType": "Conference",
            "expectedAttendees": 44
       }
       """;


        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(eventRequest)
                .when()
                .post(endpoint)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .log().all()
                .header("Location", Matchers.containsString("/api/event"))
                .contentType(ContentType.JSON)
                .body("id", Matchers.notNullValue())
                .body("eventName", Matchers.equalTo("Sample Event"))
                .body("eventType", Matchers.equalTo("Conference"))
                .body("organizerId", Matchers.equalTo(1))
                .body("expectedAttendees", Matchers.equalTo(44));
    }


    @Test
    public void testGetAllEvents() {
        long userId = 1;
        String endpoint = "/api/event" + "?userId=" + userId;

        String eventRequest = """
       {
            "eventName": "Sample Event",
            "eventType": "Conference",
            "expectedAttendees": 44
       }
       """;




        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(eventRequest)
                .when()
                .post(endpoint)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .log().all()
                .header("Location", Matchers.containsString("/api/event"))
                .contentType(ContentType.JSON)
                .body("id", Matchers.notNullValue())
                .body("eventName", Matchers.equalTo("Sample Event"))
                .body("eventType", Matchers.equalTo("Conference"))
                .body("organizerId", Matchers.equalTo(1))
                .body("expectedAttendees", Matchers.equalTo(44));


        RestAssured.given()
                .when()
                .get("/api/event/")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .log().all()
                .body("size()", Matchers.greaterThan(0))
                .body("[0].id", Matchers.notNullValue())
                .body("[0].eventName",  Matchers.equalTo("Sample Event"))
                .body("[0].eventType", Matchers.equalTo("Conference"))
                .body("[0].organizerId", Matchers.equalTo(1))
                .body("[0].expectedAttendees",  Matchers.equalTo(44));
    }



    @Test
    void testUserServiceIntegration() {
        long userId = 1;

        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8092/users/"+userId+"/role", String.class);
        System.out.println("Response Status: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());

        RestAssured.given()
                .when()
                .get("http://localhost:8092/users/"+userId+"/role")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.TEXT)
                .log().all()
                .body( Matchers.equalTo("staff"));

    }

}
