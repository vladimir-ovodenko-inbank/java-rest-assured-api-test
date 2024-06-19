package ee.inbank.rd.inheritance;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ee.inbank.rd.config.Routes;
import ee.inbank.rd.constants.Decision;
import ee.inbank.rd.specs.RequestSpecs;
import ee.inbank.rd.specs.ResponseSpecs;
import ee.inbank.rd.util.JsonUtil;
import ee.inbank.rd.util.JwtAuthenticatorWithSpec;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static ee.inbank.rd.constants.Decision.LOAN_AMOUNT_DECREASE;

public class DecisionSoftlyModifiedTest extends BaseTest {

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

    @Test
    public void postRequestTest() {

        Response riskResponse = RestAssured.given()
                .spec(authorizedSpec)
                .body(jsonBody)
                .post(Routes.RISK)
                .then()
                .spec(ResponseSpecs.getResponseSpecification())
                .and()
                .extract()
                .response();

        softly.assertThat(riskResponse.jsonPath().getString("decisionResponse.status")).isEqualTo("POSITIVE");
        softly.assertThat(riskResponse.jsonPath().getString("decisionResponse.maxSumDetails.maxSums[1].checkMaxSum")).isEqualTo("15000.0");
    }


    @Test
    public void postModifiedRequestTest() {

        // modify base body
        JsonObject jsonObject = JsonParser.parseString(jsonBody).getAsJsonObject();

        jsonObject.getAsJsonObject("config")
                .addProperty("useScavenger", true);

        JsonPath riskResponse = RestAssured.given()
                .spec(authorizedSpec)
                .body(jsonObject.toString())
                .post(Routes.RISK)
                .then()
                .spec(ResponseSpecs.getResponseSpecification())
                .and()
                .extract()
                .response()
                .getBody()
                .jsonPath();

        softly.assertThat(riskResponse.getString("decisionResponse.status")).isEqualTo(Decision.POSITIVE);
        softly.assertThat(riskResponse.getString("decisionResponse.maxSumDetails.maxSums[1].checkMaxSum")).isEqualTo("15000.0");
    }

    @Test
    public void postModifiedRequestNegativeTest() {
        // modify base body
        JsonObject jsonObject = JsonParser.parseString(jsonBody).getAsJsonObject();
        jsonObject.getAsJsonObject("decisionData")
                .getAsJsonObject("product")
                .getAsJsonObject("productReturnRisk")
                .addProperty("productGroupMaxRisk", 500);

        JsonPath riskResponse = RestAssured.given()
                .spec(authorizedSpec)
                .body(jsonObject.toString())
                .post(Routes.RISK)
                .then()
                .spec(ResponseSpecs.getResponseSpecification())
                .and()
                .extract()
                .response()
                .getBody()
                .jsonPath();

        softly.assertThat(riskResponse.getString("decisionResponse.status")).isEqualTo(LOAN_AMOUNT_DECREASE);
    }

}
