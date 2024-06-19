package ee.inbank.rd.api;

import ee.inbank.rd.config.Routes;
import ee.inbank.rd.specs.ResponseSpecs;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class ApiClient {

    public static Response createDecisionRequest(RequestSpecification requestSpecification, String requestBody) {
        return given()
                .spec(requestSpecification)
                .body(requestBody)
                .post(Routes.RISK)
                .then()
                .spec(ResponseSpecs.getResponseSpecification())
                .and()
                .extract()
                .response();
    }
}
