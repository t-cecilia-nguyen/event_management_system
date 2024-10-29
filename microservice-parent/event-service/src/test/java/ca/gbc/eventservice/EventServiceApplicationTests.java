package ca.gbc.eventservice;

import ca.gbc.eventservice.model.Booking;
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

  //  @Autowired
   // private TestRestTemplate restTemplate;


//    @Test
//    public void testEndPoint(){
//        String endpoint =  "/api/booking";
//        ResponseEntity<String> response = restTemplate.getForEntity(endpoint, String.class);
//
//       }


    @Test
    public void testCreateEvent() {
        String bookingId = "book456"; // Replace with an actual bookingId or mock data
        String endpoint = "/api/event/" + bookingId;

        String eventRequest = """
       {
            "eventName": "Sample Event",
            "eventType": "Conference",
            "expectedAttendees": 100
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
                .header("Location", Matchers.containsString("/api/event/"))
                .contentType(ContentType.JSON)
                .body("id", Matchers.notNullValue())
                .body("eventName", Matchers.equalTo("Sample Event"))
                .body("eventType", Matchers.equalTo("Conference"))
                .body("organizerId", Matchers.equalTo("USER123"))
                .body("bookingId", Matchers.equalTo(bookingId))
                .body("expectedAttendees", Matchers.equalTo(100));
    }


    @Test
    public void testGetAllEvents() {

        String bookingId = "book222"; // Replace with an actual bookingId or mock data
        String endpoint = "/api/event/" + bookingId;

        String eventRequest = """
       {
            "eventName": "Sample Event",
            "eventType": "Conference",
            "expectedAttendees": 100
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
                .header("Location", Matchers.containsString("/api/event/"))
                .contentType(ContentType.JSON)
                .body("id", Matchers.notNullValue())
                .body("eventName", Matchers.equalTo("Sample Event"))
                .body("eventType", Matchers.equalTo("Conference"))
                .body("organizerId", Matchers.equalTo("USER123"))
                .body("bookingId", Matchers.equalTo(bookingId))
                .body("expectedAttendees", Matchers.equalTo(100));



        RestAssured.given()
                .when()
                .get("/api/event/")
                .then()
                .statusCode(HttpStatus.OK.value())  // Check for 200 OK status
                .contentType(ContentType.JSON)
                .log().all()
                .body("size()", Matchers.greaterThan(0))  // Ensure there's at least one event in the response
                .body("[0].id", Matchers.notNullValue())  // Check that the first item has a non-null ID
                .body("[0].eventName",  Matchers.equalTo("Sample Event"))  // Ensure `eventName` is not null
                .body("[0].eventType", Matchers.equalTo("Conference"))  // Ensure `eventType` is not null
                .body("[0].organizerId", Matchers.equalTo("USER123"))  // Ensure `organizerId` is not null
                .body("[0].bookingId", Matchers.equalTo(bookingId))  // Ensure `bookingId` is not null
                .body("[0].expectedAttendees",  Matchers.equalTo(100));  // Ensure `expectedAttendees` is positive
    }



    @Test
    void testBookingServiceIntegration() {
        ResponseEntity<Booking> response = restTemplate.getForEntity("http://localhost:8096/api/booking/foo", Booking.class);
        System.out.println("Response Status: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());

        RestAssured.given()
                .when()
                .get("http://localhost:8096/api/booking/foo")
                .then()
                .statusCode(HttpStatus.OK.value())  // Check for 200 OK status
                .contentType(ContentType.JSON)
                .log().all()
                .body("bookingId", Matchers.equalTo("foo"))
                .body("userId", Matchers.equalTo("USER123"))
                .body("roomId", Matchers.equalTo("R123"));
    }

}
