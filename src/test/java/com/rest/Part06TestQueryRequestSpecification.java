package com.rest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.SpecificationQuerier;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Ch. 81. Query Request Specification.
 * If you have multiple specifications then you can set one of these specifications as default.
 */
@Slf4j
public class Part06TestQueryRequestSpecification {
    /**
     * Properties defined in postman.
     */
    private final String X_API_KEY = "X-Api-Key";
    private final String X_API_KEY_VALUE = "PMAK-60be82a8f120f500350530bd-5564f633c07f09b9472b464b32bbb43cc3";
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
        baseURI = "https://api.getpostman.com";
        basePath = "/collections";

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder
//                .setBaseUri(baseURI)
//                .setBasePath(basePath)
                .addHeader("X-Api-Key", X_API_KEY_VALUE)
                .log(LogDetail.ALL)
        ;

        requestSpecification = requestSpecBuilder.build();

        // A default response setup.
        // Without RestAssured.given() because RestAssured.requestSpecification has already been set.
        response = given().when().get()
                .then()
                .log().all()
                .contentType(ContentType.JSON)
                .extract()
                .response()
        ;
    }

    /**
     * Example testing the properties of the request that is being sent.
     * This is based on interface io.restassured.specification.QueryableRequestSpecification.
     */
    @Test
    public void testQuery() {
        QueryableRequestSpecification queryableRequestSpecification = SpecificationQuerier.query(requestSpecification);
        log.info("baseUri: {}", queryableRequestSpecification.getBaseUri());
        log.info("Headers: {}", queryableRequestSpecification.getHeaders());
        assertThat(queryableRequestSpecification.getBaseUri(), is(equalTo(baseURI)));
        assertThat(queryableRequestSpecification.getHeaders().getValue(X_API_KEY), is(equalTo(X_API_KEY_VALUE)));
    }

    /**
     * Overwriting the default response.
     * Note that RestAssured.given() is not necessary.
     * Reason:
     * RestAssured.resquestSpecification has already been given a value.
     */
    @Test
    public void testValidatStatusCode() {
        response = get()
                .then()
                .log().all()
                .extract()
                .response()
        ;
        assertThat(response.statusCode(), is(equalTo(HTTP_Status_code_OK)));
    }

    @Test
    public void testValidateResponseCode() {
        assertThat(response.statusCode(), is(equalTo(HTTP_Status_code_OK)));
        assertThat(response.path("workspaces[0].name").toString(), is(equalTo("Yet another Workspace")));
    }
}
