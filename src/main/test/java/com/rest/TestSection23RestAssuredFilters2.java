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

/**
 * Ch. 110. Log Request and Response Specification to the Console.
 * Ch. 111. Reuse Filters.
 * Ch. 112. Log to File.
 *
 * Resources:
 * - https://github.com/rest-assured/rest-assured/wiki/Usage#filters
 * - https://javadoc.io/static/io.rest-assured/rest-assured/4.4.0/index.html?io/restassured/RestAssured.html
 *
 */
@Slf4j
public class TestSection23RestAssuredFilters2 {

    /**
     * Local parameters
     */
    private final int HTTP_Status_code_OK = 200;
    private final int HTTP_Status_code_Created = 201;

    Response response;
    PrintStream fileOutputStream;

    @BeforeClass
    public void init() throws FileNotFoundException {
        baseURI = "http://postman-echo.com";
        basePath = "/get";

        boolean shouldPrettyPrint = true;
        // Using this stream the logging will now be placed in:
        // $HOME/Development/workspaceStudy/RestAssured/restAssured.log
        // So in the basedirectory of the project.
        fileOutputStream = new PrintStream(new File("restAssured.log"));

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();

        requestSpecBuilder
                .setBaseUri(baseURI)
                .setContentType("application/json; charset=utf-8")
                .addFilter(new RequestLoggingFilter(LogDetail.ALL, shouldPrettyPrint, fileOutputStream))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL, shouldPrettyPrint, fileOutputStream))
                // If the following it NOT commented out it will send the same logging to the console.
                .log(LogDetail.ALL)
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
     * Ch. 111. Reuse Filters.
     */
    @Test
    public void testReuseFilters() throws FileNotFoundException {

        given()
                // These filters are now placed in the requestSpecification.
                // This allows the filters to be used in each test.
                // Instead of adding the PrintStream to the RequestSpecification
                // It can also be added here in the method.
                // Thus allowing for logging to a filter per separate test method.
//                .filter(new RequestLoggingFilter(LogDetail.BODY, shouldPrettyPrint, fileOutputStream))
//                .filter(new ResponseLoggingFilter(LogDetail.STATUS, shouldPrettyPrint, fileOutputStream))
//                .log().all()
                .when()
                .get()
                .then()
                // If the following it NOT commented out it will send the same logging to the console.
                .log().all()
//                .assertThat()
//                .statusCode(HTTP_Status_code_OK)
        ;
    }
}
