package ca.gbc.eventservice.stub;


import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;


public class UserClientStub {


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
