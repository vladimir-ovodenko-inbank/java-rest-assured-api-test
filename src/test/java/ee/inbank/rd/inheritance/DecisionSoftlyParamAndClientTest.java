package ee.inbank.rd.inheritance;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ee.inbank.rd.api.ApiClient;
import ee.inbank.rd.config.Routes;
import ee.inbank.rd.specs.RequestSpecs;
import ee.inbank.rd.specs.ResponseSpecs;
import ee.inbank.rd.util.JsonUtil;
import ee.inbank.rd.util.JwtAuthenticatorWithSpec;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class DecisionSoftlyParamAndClientTest extends BaseTest {

    static String jwt;
    static String jsonBody;
    static String jsonFilePath = "src/test/resources/bodyExample.json";
    static RequestSpecification authorizedSpec;

    @BeforeAll
    static void setUp() {
        jwt = JwtAuthenticatorWithSpec.getJwtToken();
        jsonBody = JsonUtil.readJsonFromFile(jsonFilePath);
        authorizedSpec = RequestSpecs.getAuthorizedDecisionSpec(jwt);
    }


    @ParameterizedTest
    @ValueSource(ints = {16, 18, 151})
    public void postRequestModifiedWithParamsTest(int age) {

        System.out.println("Age: " + age);
        JsonObject jsonObject = JsonParser.parseString(jsonBody).getAsJsonObject();
        jsonObject.getAsJsonObject("decisionData")
                .getAsJsonObject("customer")
                .addProperty("age", age);


        Response riskResponse = RestAssured.given()
                .spec(authorizedSpec)
                .body(jsonObject.toString())
                .post(Routes.RISK)
                .then()
                .spec(ResponseSpecs.getResponseSpecification())
                .and()
                .extract()
                .response();

        softly.assertThat(riskResponse.jsonPath().getString("decisionResponse.status")).isEqualTo("POSITIVE");
        softly.assertThat(riskResponse.jsonPath().getString("decisionResponse.maxSumDetails.maxSums[1].checkMaxSum")).isEqualTo("15000.0");
    }

    @ParameterizedTest
    @ValueSource(ints = {16, 18})
    public void postRequestModifiedWithParamsAndClientTest(int age) {

        System.out.println("Age: " + age);
        JsonObject jsonObject = JsonParser.parseString(jsonBody).getAsJsonObject();
        jsonObject.getAsJsonObject("decisionData")
                .getAsJsonObject("customer")
                .addProperty("age", age);


        Response riskResponse = ApiClient.createDecisionRequest(authorizedSpec, jsonObject.toString());

        softly.assertThat(riskResponse.jsonPath().getString("decisionResponse.status")).isEqualTo("POSITIVE");
        softly.assertThat(riskResponse.jsonPath().getString("decisionResponse.maxSumDetails.maxSums[1].checkMaxSum")).isEqualTo("15000.0");
    }

}
