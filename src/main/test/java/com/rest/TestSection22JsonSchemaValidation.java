package com.rest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.responseSpecification;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Slf4j
public class TestSection22JsonSchemaValidation {

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
                .log(LogDetail.ALL);
        requestSpecification = requestSpecBuilder.build();

        // Note that this will not show the logging. '.log(LogDetail.ALL)' Doesn't work.
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder()
                .expectStatusCode(HTTP_Status_code_OK)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL);

        responseSpecification = responseSpecBuilder.build();
        log.info("init");
    }

    /**
     * Ch. 108. Automate JSON Schema Validation.
     *
     * https://github.com/rest-assured/rest-assured/wiki/Usage#json-schema-validation
     * - you can validate that a resource (/products) conforms with the schema:
     *
     *   get("/products").then().assertThat().body(matchesJsonSchemaInClasspath("products-schema.json"));
     *
     * In the following the json response part is validated against a json-schema
     * that has been created in https://jsonschema.net/home.
     * The json-schema is present in:
     * - src/test/resources/postman-echo-json-schema.json
     *      - Remove the samples from the fields.
     *      - Also remove the id's:
     *           - "$id": "http://example.com/example.json",
     *           - etc.
     *      - Remove empty arrays that are required: If required they should be filled.
     * Most important properties to check:
     * - Whether a field is mandatory.
     * - The type of a field.
     * - What the default value is.
     *
     * In practice: ensure that the development teams hands over a json-schema.
     *
     * File: src/test/resources/postman-echo-json-schema.json
     * - Contains the json schema of the json response.
     */
    @Test
    public void testJsonSchema() {
        given()
                .log().all()
                .when()
                .get()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HTTP_Status_code_OK)
                .body(matchesJsonSchemaInClasspath("postman-echo-json-schema.json"));
        ;
    }
}
