package com.rest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Ch. 92. Send JSON Array as a List - Build Test Case.
 * The example in the course videou does not immediately work. However when I executed this it does work.
 * The tutor indicates that the encoding (utf-8) is not set explicitly.
 *
 * In the response I can see: Content-Type: application/json; charset=utf-8.
 * This has been set in Postman.
 *
 * Ch. 93. Send JSON Array as a List - Content Type Encoding.
 *
 * application/json
 * application/json;charset=utf-8
 *
 * UTF -> Unicode Transformational Format
 * UTF-8 -> Encoding format supported by HTML5 by default.
 *
 * Unicode -> List of characters represented by unique decimaal numbers.
 * Encoding -> Mechanism of converting unicode chaaracters to binary numbers.
 *
 *
 */
@Slf4j
public class Part092TestPostPayloadAsJsonArray {

    /**
     * Local parameters
     */
    private final int HTTP_Status_code_OK = 200;
    private final int HTTP_Status_code_Created = 201;

    Response response;

    @BeforeClass
    public void init() {
        // The base Url of the Mock server that has been set up in Postman.
        baseURI = "https://0cfbbd01-fbce-453b-91c1-41c2b17b356d.mock.pstmn.io";
        // The endpoint. Now .when().post() can remain empty.
        // Otherwise: .when().post(<endpoint>).
        basePath = "/post";

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        /**
         * The .setConfig() below is necessary if for some reason utf-8 is NOT used as default.
         * Alternatively:
         * It is also possible to set the .setContentType() as follows:
         * .setContentType("application/json; charset=utf-8")
         * If this is use this should also be set in Postman!
         * In the logging you can see:
         * - Content-Type=application/json; charset=utf-8
         *
         * See also:
         * - https://github.com/rest-assured/rest-assured/wiki/Usage#avoid-adding-the-charset-to-content-type-header-automatically
         *
         */
        requestSpecBuilder
                .setBaseUri(baseURI)
                .addHeader("x-mock-match-request-body", "true")
//                .setContentType(ContentType.JSON)
                .setContentType("application/json; charset=utf-8")
                // Apparently the author came across this error (and this is a difficult one to solve).
                // The following is not necessary if the charset is defined explicitly as done above.
                // Setting the charset explicitly should in that case also be done at the source (or mock).
//                .setConfig(config.encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .log(LogDetail.ALL);
        requestSpecification = requestSpecBuilder.build();

        // Note that this will not show the logging. '.log(LogDetail.ALL)' Doesn't work.
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder()
                .expectStatusCode(HTTP_Status_code_OK)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL);

        responseSpecification = responseSpecBuilder.build();

//        // Alternatively setup. Works nicely too. Again: here '.log(LogDetail.ALL)' would not work.
//        responseSpecification = RestAssured.expect()
//                .statusCode(HTTP_Status_code_OK)
//                .contentType(ContentType.JSON);
    }

    @Test
    public void testPostRequestPayloadJsonArrayAsList() {

        /**
         * Json body in the mock:
         * [
         *     { "id": "5001", "type": "None" },
         *     { "id": "5002", "type": "Glazed" }
         * ]
         *
         */
        Map<String, String> object5001 = new HashMap<>();
        object5001.put("id", "5001");
        object5001.put("type", "None");
        Map<String, String> object5002 = new HashMap<>();
        object5002.put("id", "5002");
        object5002.put("type", "Glazed");

        List<Map<String, String>> jsonList = new ArrayList<>();
        jsonList.add(object5001);
        jsonList.add(object5002);

        /**
         * Since the content-type is "application/json" then REST Assured will automatically try to
         * serialize the object using Jackson  or Gson  if they are available in the classpath.
         *
         * Again:
         * An instance of an implementation of interface io.restassured.specificationResponseSpecification
         * has already been created.
         * However it is always possible to create another instance and use that:
         * .then().spec(<responseSpecification).
         */
        given()
                .body(jsonList)
                .when()
                .post()
                .then()
//                .spec(responseSpecification)
                .log().all()
                .assertThat()
                .body("message", is(equalTo("Success")));
    }
}
