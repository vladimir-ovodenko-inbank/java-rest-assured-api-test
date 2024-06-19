package ee.inbank.rd.util;

import ee.inbank.rd.config.Routes;
import ee.inbank.rd.specs.RequestSpecs;
import ee.inbank.rd.specs.ResponseSpecs;
import io.restassured.RestAssured;

public class JwtAuthenticatorWithSpec {

    public static String getJwtToken() {
        // no log
        // content-type
        // no base url
        return RestAssured
                .given()
                .spec(RequestSpecs.getJwtSpec())
                .when()
                .get(Routes.AUTH + "trusted?instance=lt1")
                .then()
                .spec(ResponseSpecs.getResponseSpecification())
                .extract()
                .response()
                .jsonPath()
                .getString("jwt");
    }
}
