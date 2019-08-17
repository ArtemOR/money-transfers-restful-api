package api.rest;


import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.*;

public class MoneyTransfersRorRestTest {

    @BeforeClass
    public static void setUriAndPort() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8082;
    }

    @Test public void testCreateUser() {
        get("/user/123")
                .then()
                .assertThat()
                .statusCode(404);

        given().body("{\n" +
                "    \"name\": \"testUser\",\n" +
                "    \"passportId\": \"123\"\n" +
                "}").
                when().
                post("/user").
                then().
                assertThat().
                statusCode(200);

        get("/user/123")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test public void testDeleteUser() {
        get("/user/123")
                .then()
                .assertThat()
                .statusCode(200);

        delete("/user/123")
                .then()
                .assertThat()
                .statusCode(204);

        get("/user/123")
                .then()
                .assertThat()
                .statusCode(404);
    }
}
