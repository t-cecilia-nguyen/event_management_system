package ca.gbc.approvalservice;

import ca.gbc.approvalservice.stub.UserClientStub;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
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

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApprovalServiceApplicationTests {

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
    public void testCreateApproval() {
        long userId =4;

        String approvalRequest = String.format("""
       {
        "eventId": "672da5511bb7ef5e5d4b3ff9",
        "approverId": "4",
        "comments": "Approval comment here"
       }
       """, userId);



        //user client stub
//        UserClientStub.stubCheckUserType(userId);


        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(approvalRequest)
                .when()
                .post("/api/approval")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .log().all()
                .header("Location", Matchers.containsString("/api/approval"))
                .contentType(ContentType.JSON)
                .body("id", Matchers.notNullValue())
                .body("eventId", Matchers.notNullValue())
                .body("approverId", Matchers.equalTo("4"))
                .body("approved", Matchers.equalTo("PENDING"))
                .body("comments", Matchers.equalTo("Approval comment here"));
    }


    @Test
    public void testGetAllApprovals() {



        RestAssured.given()
                .when()
                .get("/api/approval")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .log().all();
    }


    @Test
    void testDeleteApproval() {

        String approvalId = "123";


        RestAssured.given()
                .when()
                .delete("/api/approval/{approvalId}", approvalId)
                .then()
                .log().all()
                .statusCode(204);

    }


}
