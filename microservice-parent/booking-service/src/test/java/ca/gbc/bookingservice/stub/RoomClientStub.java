package ca.gbc.bookingservice.stub;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class RoomClientStub {

    public static void roomClientCall(Long roomId) {

        stubFor(get(urlPathEqualTo("/rooms/" + roomId + "/availability"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));
    }
}
