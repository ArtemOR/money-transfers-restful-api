package api.rest;

import io.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class MoneyTransferTest {

    @BeforeClass
    public static void init() {
        MoneyTransferRest.start();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8082;
    }

    @AfterClass
    public static void stopServer() {
        MoneyTransferRest.stop();
    }

}
