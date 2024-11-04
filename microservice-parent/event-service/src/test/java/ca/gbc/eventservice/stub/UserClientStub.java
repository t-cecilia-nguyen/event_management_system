package ca.gbc.eventservice.stub;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;


public class UserClientStub {

    public static void stubUserCall(Long userId) {

        stubFor(get(urlEqualTo("/users/" +userId+"/role"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("staff")
            )
        );
    }
}
