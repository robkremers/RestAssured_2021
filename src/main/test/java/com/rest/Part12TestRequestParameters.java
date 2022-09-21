package com.rest;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

/**
 * Ch. 96. Postman Echo Introduction.
 * Ch. 97. Single Query Parameter.
 *
 * http://postman-echo.com is used for sending back a response
 * that contains parameters, headers, body + a number of specified header parameters.
 *
 * Resources:
 * - https://learning.postman.com/docs/developer/echo-api/
 * - https://www.postman.com/postman/workspace/published-postman-templates/documentation/631643-f695cab7-6878-eb55-7943-ad88e1ccfd65?ctx=documentation
 *
 *
 */
@Slf4j
public class Part12TestRequestParameters {

    /**
     * Local parameters
     */
    private final int HTTP_Status_code_OK = 200;
    private final int HTTP_Status_code_Created = 201;

    @BeforeClass
    public void init() {
        // The base Url of the Mock server that has been set up in Postman.
        baseURI = "http://postman-echo.com";
        basePath = "/get";
    }

    /**
     * param() (general) and queryParam (specific) can be used.
     * In the logging:
     * Request params:	foo1=bar1
     * Query params:	foo2=bar2
     * In the response:
     *     "args": {
     *         "foo1": "bar1",
     *         "foo2": "bar2"
     *     }
     */
    @Test
    public void testSingleQueryParameter() {
        given()
                .param("foo1", "bar1")
                .queryParam("foo2", "bar2")
                .log().all()
                .when()
                .get()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HTTP_Status_code_OK);
    }

    /**
     * Ch. 98. Multiple Query Parameters.
     */
    @Test
    public void testMultipleQueryParameter() {
        given()
                .queryParam("foo1", "bar1")
                .queryParam("foo2", "bar2")
                .log().all()
                .when()
                .get()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HTTP_Status_code_OK);
    }

    /**
     * Ch. 98. Multiple Query Parameters.
     */
    @Test
    public void testMultipleQueryParameterUsingHashMap() {
        Map<String, String> queryParametermap = new HashMap<>();
        queryParametermap.put("foo1", "bar1");
        queryParametermap.put("foo2", "bar2");

        given()
//                .queryParams(queryParametermap)
                .queryParams("foo1", "bar1", "foo2", "bar2")
                .log().all()
                .when()
                .get()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HTTP_Status_code_OK);
    }

    /**
     * Ch. 99. Multi Value Query Parameter.
     * In order to separate the multiple values for the parameters
     * comma's or semi-colons can be used.
     * Request URI:	http://postman-echo.com/get?foo1=bar1%2Cbar2%2Cbar3
     * "args": {
     *         "foo1": "bar1,bar2,bar3"
     *     }
     * or
     * Request URI:	http://postman-echo.com/get?foo1=bar1%3Bbar2%3Bbar3
     * "args": {
     *         "foo1": "bar1;bar2;bar3"
     *     }
     */
    @Test
    public void testMultiValueQueryParameter() {
        given()
                .queryParam("foo1", "bar1,bar2,bar3")
                .log().all()
                .when()
                .get()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HTTP_Status_code_OK);
    }
}
