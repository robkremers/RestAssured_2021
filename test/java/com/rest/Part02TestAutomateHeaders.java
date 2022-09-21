package com.rest;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.ExtractableResponse;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.*;

@Slf4j
public class Part02TestAutomateHeaders {

    private final int HTTP_Status_code_OK = 200;
    private final int HTTP_Status_code_Created = 201;

    /**
     * URI to a mock server defined in Postman.
     */
    @BeforeClass
    public static void init() {
        baseURI = "https://0cfbbd01-fbce-453b-91c1-41c2b17b356d.mock.pstmn.io";
        basePath = "/get";
    }

    /**
     * Ch. 69. Multiple Headers in Request.
     * <p>
     * Situation that we want to use io.restassured.http.Headers in order to keep the io.restassured.given()
     * relatively compact.
     */
    @Test
    public void testMultipleHeaders() {

        Header header = new Header("header", "value1");
        Header matchHeader = new Header("x-mock-match-request-headers", "header");
        given()
                .header(header)
//                .header("header", "value1", "x-mock-match-request-headers", "header")
                .header(matchHeader)
                .when()
                .get()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HTTP_Status_code_OK);
    }

    /**
     * Ch. 70. Multiple Headers in Request - Using Headers.
     */
    @Test
    public void testMultipleHeadersUsingHeaders() {
        Header header = new Header("header", "value1");
        Header matchHeader = new Header("x-mock-match-request-headers", "header");

        Headers headers = new Headers(header, matchHeader);

        given()
                .headers(headers)
                .when()
                .get()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HTTP_Status_code_OK)
        ;
    }

    /**
     * Ch. 71. Multiple Headers in Request - Using Map.
     */
    @Test
    public void testMultipleHeadersUsingMap() {

        Map<String, String> headers = new HashMap<>();
        headers.put("header", "value2");
        // The following is optional.
        headers.put("x-mock-match-request-headers", "header");

        given()
                .headers(headers)
                .when()
                .get()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HTTP_Status_code_OK);
    }

    /**
     * Ch 72. Multi Value Header
     * With regards to the functionality of Postman Mocks:
     * Apparently a header parameter with a name containing 'header' can only be named 'header.
     * Otherwise an error will occur.
     * But it's entirely possible to give a header parameter another name NOT containing 'header'.
     */
    @Test
    public void testMultipleHeadersInRequest() {
//        Header header1 = new Header("multiValueHeader", "value1");
//        Header header2 = new Header("multiValueHeader", "value2");
//        Headers headers = new Headers(header1, header2);

        Header header1 = new Header("number1", "three");
        Header header2 = new Header("number2", "four");
        // The following is optional.
        Header header3 = new Header("x-mock-match-request-headers", "number1,number2");
        Headers headers = new Headers(header1, header2);

        given()
                .headers(headers)
                .log().headers()
                .when()
                .get()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HTTP_Status_code_OK);
    }

    /**
     * Ch. 73. Response Headers - Assert
     * So now we will look at the response whereas previously we looked at the headers of the request.
     * <p>
     * Result:
     * - If successful: the normal result will be displayed.
     * - If wrong:
     * java.lang.AssertionError: 1 expectation failed.
     * Expected header "responseHeader" was not "responseValue2", was "responseValue1". Headers are:
     * +
     * All headers. So the headers are now shown twice: 1) via .log().all() and 2) via the error.
     */
    @Test
    public void testAssertResponseHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("header", "value1");
        headers.put("x-mock-match-request-headers", "header");

        given()
                .headers(headers)
                .when()
                .get()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HTTP_Status_code_OK)
//                .header("responseHeader", "responseValue1")
//                .header("X-RateLimit-Limit", "120")
                // or
                .headers("responseHeader", "responseValue1"
                        , "X-RateLimit-Limit", "120")
        ;
    }

    /**
     * Ch. 74.Response Headers - Extract.
     * Purpose:
     * - Extract the header information for use elsewhere.
     *
     */
    @Test
    public void testExtractResponseHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("header", "value1");
        headers.put("x-mock-match-request-headers", "header");

        Headers responseHeaders = given()
                .headers(headers)
                .when()
                .get()
                .then()
//                .log().all()
                .assertThat()
                .statusCode(HTTP_Status_code_OK)
                .extract()
                .headers()
                ;

        log.info("Overview of all extracted response headers:");
        responseHeaders.forEach( (Header header) -> {
            log.info("header " + header.getName() + "  = " + header.getValue());
        });
        log.info("Value of the response header \"responseHeader\"");
        log.info("Response header \"responseHeader\" = " + responseHeaders.get("responseHeader").getValue());
    }

    /**
     * Ch. 75. Response Headers - Extract Multi Value Header.
     *
     * In the situation below the following is visible in the logging:
     * header mutliValueHeader  = responseValue2, responseValue3
     */
    @Test
    public void testExtractMultiValueResponseHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("header", "value1");
        headers.put("x-mock-match-request-headers", "header");

        Headers responseHeaders = given()
                .headers(headers)
                .when()
                .get()
                .then()
//                .log().all()
                .assertThat()
                .statusCode(HTTP_Status_code_OK)
                .extract()
                .headers()
                ;
        log.info("Overview of all extracted response headers:");
        responseHeaders.forEach( (Header header) -> {
            log.info("header " + header.getName() + "  = " + header.getValue());
        });

        log.info("Collect the multiple values of response header parameter \"multiValueHeader\"");
        List<String> multiValues = responseHeaders.getValues("multiValueHeader");
        multiValues.forEach( (String value) -> {
            log.info("Value: " + value);
        });
    }
}
