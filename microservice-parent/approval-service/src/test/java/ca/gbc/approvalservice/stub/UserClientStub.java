package ca.gbc.approvalservice.stub;


import com.github.tomakehurst.wiremock.client.WireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;


public class UserClientStub {

    public static void stubCheckUserType(Long userId) {
        stubFor(get(urlEqualTo("/users/" + userId + "/usertype"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("STAFF")));  // Ensure response is in correct format
        System.out.println("Stubbed user type check for user: " + userId);
        WireMock.verify(getRequestedFor(urlEqualTo("/users/4/usertype")));

    }

    public static void stubUserIdExistCall(Long userId) {
        stubFor(get(urlEqualTo("/users/exist/" + userId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));
    }

    public static void stubUserRoleCall(Long userId) {
        stubFor(get(urlEqualTo("/users/" + userId + "/usertype"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("\"staff\"")));
    }


}
