package com.rest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import io.restassured.filter.log.LogDetail;

/**
 * Ch. 79. Request Specification Builder.
 * <p>
 * Note:
 * Due to the response being created in the @BeforeAll init() method
 * The logging regarding request and response will now be printed only once.
 * Even if all test methods will be executed.
 *
 */
@Slf4j
public class Part04TestRequestSpecificationExample {

    /**
     * Properties defined in postman.
     */
    private final String X_API_KEY = "X-Api-Key";
    private final String X_API_KEY_VALUE = "<X_API_KEY_VALUE>";
    private final int HTTP_Status_code_OK = 200;
    private final int HTTP_Status_code_Created = 201;

    RequestSpecification requestSpecification;

    Response response;

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

        RequestSpecification requestSpecification = requestSpecBuilder.build();

        response = given(requestSpecification)
                .get()
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
