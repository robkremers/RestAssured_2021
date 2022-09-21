package com.rest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 *  Ch. 80. Default Request Specification.
 *  If you have multiple specifications then you can set one of these specifications as default.
 */
@Slf4j
public class Part05TestDefaultRequestSpecification {
    /**
     * Properties defined in postman.
     */
    private final String X_API_KEY = "X-Api-Key";
    private final String X_API_KEY_VALUE = "<X_API_KEY_VALUE>";
    private final int HTTP_Status_code_OK = 200;
    private final int HTTP_Status_code_Created = 201;

    Response response;

    /**
     * Again:
     * See the class io.restassured.RestAssured:
     * - baseURI
     * - basePath
     * - requestSpecification
     * are alle static parameters of this class.
     */
    @BeforeClass
    public void init() {
        baseURI = "https://api.postman.com";
        basePath = "/workspaces";

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder
                .setBaseUri(baseURI)
                .setBasePath(basePath)
                .addHeader("X-Api-Key", X_API_KEY_VALUE)
                .log(LogDetail.ALL)
        ;

        requestSpecification = requestSpecBuilder.build();

        /**
         * Note 1:
         * RestAssured.given() now has been left out.
         * The reason is that the situation has already been defined above using the RequestSpecBuilder.
         *
         * Note 2:
         * In this case the same response is used in multiple tests.
         * Depending on the situation in a test the response may have to be adapted or set up separately.
         */
        response = get()
                .then()
                .log().all()
                .extract()
                .response()
        ;
    }

    @Test
    public void testValidatStatusCode() {
        assertThat(response.statusCode(), is(equalTo(HTTP_Status_code_OK)));
    }

    @Test
    public void testValidateResponseCode() {
        assertThat(response.statusCode(), is(equalTo(HTTP_Status_code_OK)));
        assertThat(response.path("workspaces[0].name").toString(), is(equalTo("Yet another Workspace")));
    }
}
