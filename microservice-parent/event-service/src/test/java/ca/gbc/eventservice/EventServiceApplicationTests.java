package ca.gbc.eventservice;

import ca.gbc.eventservice.stub.UserClientStub;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.MongoDBContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventServiceApplicationTests {

    private WireMockServer wireMockServer;

    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8080));
        wireMockServer.start();

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @AfterEach
    void teardown() {
        wireMockServer.stop();
    }

    static {
        mongoDBContainer.start();
    }



    @Test
    public void testCreateEvent() {
        long userId =1;

        String eventRequest = String.format("""
       {
            "eventName": "Sample Event",
            "eventType": "Conference",
            "organizerId": %d,
            "expectedAttendees": 44
       }
       """, userId);


        //user client stub
        UserClientStub.stubUserIdExistCall(userId);

        UserClientStub.stubUserRoleCall(userId);
        //--> will hardcoded returning staff


        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(eventRequest)
                .when()
                .post("/api/event")
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

        long userId =1;

        String eventRequest = String.format("""
       {
            "eventName": "Sample Event",
            "eventType": "Conference",
            "organizerId": %d,
            "expectedAttendees": 44
       }
       """, userId);


        //user client stub
        UserClientStub.stubUserIdExistCall(userId);
        //--> will hardcoded returning true


        UserClientStub.stubUserRoleCall(userId);
        //--> will hardcoded returning staff


        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(eventRequest)
                .when()
                .post("/api/event")
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
                .get("/api/event")
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
    void updateEventTest() {
        long userId =1;
        String eventId= "EVENT1";

        String updateEventRequest = String.format("""
       {
            "eventName": "Updated Sample Event",
            "eventType": "Conference",
            "organizerId": %d,
            "expectedAttendees": 44
       }
       """, userId);

        //user client stub
        UserClientStub.stubUserIdExistCall(userId);

        UserClientStub.stubUserRoleCall(userId);
        //--> will hardcoded returning staff

        RestAssured.given()
                .contentType("application/json")
                .body(updateEventRequest)
                .when()
                .put("/api/event/" + eventId)
                .then()
                .log().all()
                .statusCode(204)
                .header("Location", "/api/event/" + eventId);
    }

    @Test
    void deleteEventTest() {

        long userId =1;

        String eventRequest = String.format("""
       {
            "eventName": "To-be-Deleted Sample Event",
            "eventType": "Conference",
            "organizerId": %d,
            "expectedAttendees": 44
       }
       """, userId);


        //user client stub
        UserClientStub.stubUserIdExistCall(userId);
        //--> will hardcoded returning true


        UserClientStub.stubUserRoleCall(userId);
        //--> will hardcoded returning staff

        String eventId =  RestAssured.given()
                            .contentType("application/json")
                            .body(eventRequest)
                            .when()
                            .post("/api/event")
                            .then()
                            .log().all()
                            .statusCode(201)
                            .body("id", Matchers.notNullValue())
                            .extract().path("id");



        RestAssured.given()
                .when()
                .delete("/api/event/" + eventId)
                .then()
                .log().all()
                .statusCode(204);


    }




}
