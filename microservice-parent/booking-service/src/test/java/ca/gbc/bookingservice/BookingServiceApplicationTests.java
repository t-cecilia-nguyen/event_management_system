package ca.gbc.bookingservice;

import ca.gbc.bookingservice.stub.RoomClientStub;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;

import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingServiceApplicationTests {

	@ServiceConnection
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

	@LocalServerPort
	private Integer port;

	private String bookingId;

	private static WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(8080));

	@BeforeAll
	static void setupWireMockServer() {
		wireMockServer.start();
	}

	@AfterAll
	static void stopWireMockServer() {
		wireMockServer.stop();
	}

	static {
		mongoDBContainer.start();
	}

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;

		RoomClientStub.roomClientCall(1L);

		bookingId = createBookingSetup();
	}

	private String createBookingSetup() {
		String requestBody = """
				{	
					"userId": "user1",
					"roomId": "1",
					"startTime": "2024-11-10T10:00:00",
					"endTime": "2024-11-10T13:00:00",
					"purpose": "Team Meeting"
				}
		""";

		return RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/bookings")
				.then()
				.log().all()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.extract().path("id");
	}

	@AfterEach
	void cleanup(TestInfo testInfo) {
		if (!testInfo.getDisplayName().equals("deleteBookingTest()")) {
			deleteBookingSetup(bookingId);
		}
	}

	private void deleteBookingSetup(String bookingId) {
		RestAssured.given()
				.when()
				.delete("/bookings/" + bookingId)
				.then()
				.log().all()
				.statusCode(204);
	}

	@Test
	void createBookingTest() {
		String requestBody = """
				{	
					"userId": "user2",
					"roomId": "1",
					"startTime": "2024-11-10T14:00:00",
					"endTime": "2024-11-10T18:00:00",
					"purpose": "Team Presentation"
				}
		""";

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/bookings")
				.then()
				.log().all()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("userId", Matchers.equalTo("user2"))
				.body("roomId", Matchers.equalTo(1))
				.body("purpose", Matchers.equalTo("Team Presentation"));
	}

	@Test
	void getAllBookingsTest() {

		RestAssured.given()
				.when()
				.get("/bookings")
				.then()
				.log().all()
				.statusCode(200)
				.body("size()", Matchers.greaterThan(0))
				.body("[0].id", Matchers.notNullValue());
	}

	@Test
	void getBookingByIdTest() {
		RestAssured.given()
				.when()
				.get("/bookings/" + bookingId)
				.then()
				.log().all()
				.statusCode(200)
				.body("id", Matchers.equalTo(bookingId));
	}

	@Test
	void updateBookingTest() {
		String updatedRequestBody = """
				{	
					"userId": "user1",
					"roomId": "1",
					"startTime": "2024-11-10T15:00:00",
					"endTime": "2024-11-10T17:00:00",
					"purpose": "Team Meeting Updated"
				}
		""";

		RestAssured.given()
				.contentType("application/json")
				.body(updatedRequestBody)
				.when()
				.put("/bookings/" + bookingId)
				.then()
				.log().all()
				.statusCode(204)
				.header("Location", "/bookings/" + bookingId);
	}

	@Test
	void deleteBookingTest() {
		deleteBookingSetup(bookingId);

		RestAssured.given()
				.when()
				.get("/bookings/" + bookingId)
				.then()
				.log().all()
				.statusCode(404);
	}
}