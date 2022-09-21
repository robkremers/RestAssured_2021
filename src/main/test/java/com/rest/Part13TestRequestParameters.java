package com.rest;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

@Slf4j
public class Part13TestRequestParameters {
    /**
     * Local parameters
     */
    private final int HTTP_Status_code_OK = 200;
    private final int HTTP_Status_code_Created = 201;

    @BeforeClass
    public void init() {
        // The base Url of the website that provides the content.
        // This is a free website that provides content for test purposes.
        baseURI = "https://reqres.in/";
        basePath = "api/users/";
    }

    /**
     * Ch. 100. Path Parameter.
     *
     * The resulting Request URI will be:
     * Request URI:	https://reqres.in/api/users/2
     * Path params:	userId=2
     *
     * In case of multiple path parameters a HashMap + .pathParams() can be used.
     */
    @Test
    public void testBasePath() {
        given()
                .pathParam("userId", "2")
                .log().all()
                .when()
                .get("{userId}")
                .then()
                .log().all()
                .assertThat()
                .statusCode(HTTP_Status_code_OK);

    }

}
