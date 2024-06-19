package ee.inbank.rd.inheritance;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {

    protected SoftAssertions softly;

    @BeforeEach
    public void setSoftly() {
        softly = new SoftAssertions();
    }

    @AfterEach
    public void tearDown() {
        softly.assertAll();
    }
}
