package com.rest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.responseSpecification;

@Slf4j
public class TestSection23RestAssuredFilters1 {

    /**
     * Local parameters
     */
    private final int HTTP_Status_code_OK = 200;
    private final int HTTP_Status_code_Created = 201;

    Response response;

    @BeforeClass
    public void init() {
        baseURI = "http://postman-echo.com";
        basePath = "/get";

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();

        requestSpecBuilder
                .setBaseUri(baseURI)
                .setContentType("application/json; charset=utf-8")
        // This logging is put on comment because this functionality is
        // taken over by using the logging filters in the methods.
//                .log(LogDetail.ALL)
        ;
        requestSpecification = requestSpecBuilder.build();

        // Note that this will not show the logging. '.log(LogDetail.ALL)' Doesn't work.
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder()
                .expectStatusCode(HTTP_Status_code_OK)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL);

        responseSpecification = responseSpecBuilder.build();
    }

    /**
     * Ch. 110. Log Request and Response Specification to the Console.
     * <p>
     * https://github.com/rest-assured/rest-assured/wiki/Usage#filters
     * If necessary we can further detail what logging we want, e.g. using LogDetail.BODY.
     * Etc.
     * In this case, because we are using GET the body is empty:
     * Body:			<none>
     */
    @Test
    public void testLoggingFilter() {
        given()
                .filter(new RequestLoggingFilter(LogDetail.BODY))
                .filter(new ResponseLoggingFilter(LogDetail.STATUS))
//                .log().all()
                .when()
                .get()
                .then()
//                .log().all()
                .assertThat()
                .statusCode(HTTP_Status_code_OK);
    }
}
