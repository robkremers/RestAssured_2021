package com.rest;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * This topic describes the use of interface io.restassured.specification.Part03TestRequestSpecification.
 * <p>
 * It also shows to use an @BeforeClass method to allow to use repetitive methods only once.
 */
@Slf4j
public class Part03TestRequestSpecification {

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

        /**
         * The difference between 'with()' and 'given()' is syntactical.
         * Their functionality is the same.
         * If however we want to move from BDD- to non-BDD style we should use 'with()'.
         */
        requestSpecification = with()
                .header("X-Api-Key", X_API_KEY_VALUE)
                .log().all();

        response = requestSpecification
                .get()
                .then()
                .log().all()
                .extract()
                .response()
                ;
    }

    /**
     * CH. 76. What is Request Specification.
     * Example to show the use of the interface RequestSpecification.
     */
    @Test
    public void testValidatStatusCode() {

        // BDD-style
        given()
                .spec(requestSpecification)
                .when()
                .get()
                .then()
                .log().all()
                .contentType(ContentType.JSON)
                .assertThat()
                .statusCode(HTTP_Status_code_OK)
        ;

        // Non-BDD-style
        requestSpecification
                .get()
                .then()
                .log().all()
                .contentType(ContentType.JSON)
                .assertThat()
                .statusCode(HTTP_Status_code_OK)
        ;

        // If we use 'response' below as a base we can always receive loggig over get() and then(),
        // so from the sent request and received response.
        // And we have to do this only once.
        // So this can be moved to the @BeforeAll init() method.
//        Response response = requestSpecification
//                .get()
//                .then()
//                .log().all()
//                .extract()
//                .response()
//                ;

        assertThat(response.statusCode(), is(equalTo(HTTP_Status_code_OK)));

    }

    /**
     * Ch. 77. How to Reuse Request Specification.
     * The example below is actually tricky since json is not obliged to return content in a given order.
     */
    @Test
    public void testValidateResponseCode() {
        given().spec(requestSpecification)
                .when()
                .get()
                .then()
                .log().all()
                .contentType(ContentType.JSON)
                .assertThat()
                .statusCode(HTTP_Status_code_OK)
                .body("workspaces[0].name", is(equalTo("Yet another Workspace")))
        ;

        // Now:
//        Response response = requestSpecification
//                .get()
//                .then()
//                .log().all()
//                .extract()
//                .response()
//                ;
        assertThat(response.statusCode(), is(equalTo(HTTP_Status_code_OK)));
        // The following does not work anymore: other response type; compare with the statement above.
//        assertThat(response.body("workspaces[0].name", is(equalTo("Yet another Workspace")));
        // or:
        assertThat(response.path("workspaces[0].name").toString(), is(equalTo("Yet another Workspace")));
    }

}
