package com.rest;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
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
 * Ch. 82. Create Response Specification.
 * Using:
 * esponseSpecification = RestAssured.expect()
 *
 * Ch. 83. Response Specification Builder.
 * Using ResponseSpecBuilder.
 *
 * Ch. 84. Default Response Specification.
 */
@Slf4j
public class Part07TestResponseSpecification {
    /**
     * Properties defined in postman.
     */
    private final String X_API_KEY = "X-Api-Key";
    private final String X_API_KEY_VALUE = "PMAK-60be82a8f120f500350530bd-5564f633c07f09b9472b464b32bbb43cc3";
    private final int HTTP_Status_code_OK = 200;
    private final int HTTP_Status_code_Created = 201;

    Response response;

    @BeforeClass
        public void init() {
            baseURI = "https://api.postman.com";
        basePath = "/workspaces";

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder
                .addHeader("X-Api-Key", X_API_KEY_VALUE)
                .log(LogDetail.ALL)
        ;
        requestSpecification = requestSpecBuilder.build();

        /**
         * If the log().all() is added here no response is given.
         * This is already present in the lecture and has not been repaired.
         * So this is left on comment.
         */
//        responseSpecification = RestAssured.expect()
//                .statusCode(HTTP_Status_code_OK)
//                .contentType(ContentType.JSON)
//                .log().all()
        ;

        // In this case the response logging is fine. (so for now this is the preferred method).
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder()
                .expectStatusCode(HTTP_Status_code_OK)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                ;

        /**
         * In case the RestAssured.responseSpecification is used .spec(responseSpecification) is not necessary
         * in the response.
         */
        responseSpecification = responseSpecBuilder.build();

        response = given()
                .when()
//                .log().all()
                .get()
                .then()
//                .spec(responseSpecification)
                .log().all()
                .extract()
                .response();
    }

    /**
     * Note that given().when() is not necessary because the requestSpecification has already been filled in.
     */
    @Test
    public void testResponseGenerallyOkay() {
        log.info("*** Execution of testResponseGenerallyOkay");
        get()
                .then().spec(responseSpecification)
                .log().all()
        ;
    }

    @Test
    public void testValidateResponseCode() {
        log.info("*** Execution of testValidateResponseCode");
        assertThat(response.path("workspaces[0].name").toString(), is(equalTo("Yet another Workspace")));
    }
}
