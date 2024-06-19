package ee.inbank.rd.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RequestSpecs {

    public static RequestSpecification getBaseRequestSpec() {

        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://staging-apigateway.inbank.eu/api/")
                .addFilter(new ResponseLoggingFilter())//log request and response for better debugging. You can also only log if a requests fails.
                .addFilter(new RequestLoggingFilter())
                .addHeader("x-api-name", "lendify")
                .addHeader("x-api-key", "dS9bEkvE79WjMBbpaAsq5o0A233qHfrAT")
                .build();
    }

    public static RequestSpecification getJwtSpec() {
        return new RequestSpecBuilder()
                .addRequestSpecification(getBaseRequestSpec())
                .addHeader("X-Application", "lendify")
                .build();
    }


    public static RequestSpecification getAuthorizedDecisionSpec(String jwt) {
        return new RequestSpecBuilder()
                .addRequestSpecification(getBaseRequestSpec())
                .addHeader("X-Request-Id", "4ff5552d-f964-4cbc-96bb-16b949d827f3")
                .addHeader("X-Session-Context", jwt)
                .build();
    }


}
