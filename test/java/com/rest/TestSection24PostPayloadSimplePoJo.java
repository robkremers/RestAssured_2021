package com.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.entities.SimplePoJo;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Ch. 122. Simple POJO: Serialization.
 * Jackson will be used to serialize a POJO Java class instance into JSON.
 *
 * Ch. 123. Simple POJO: De-serialization.
 *
 */
@Slf4j
public class TestSection24PostPayloadSimplePoJo {
    /**
     * Local parameters
     */
    private final int HTTP_Status_code_OK = 200;
    private final int HTTP_Status_code_Created = 201;

    Response response;

    SimplePoJo simplePoJo;

    @BeforeClass
    public void init() {
        // The base Url of the Mock server that has been set up in Postman.
        baseURI = "https://0cfbbd01-fbce-453b-91c1-41c2b17b356d.mock.pstmn.io";
        basePath = "/postSimplePojo";

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder
                .setBaseUri(baseURI)
                .addHeader("x-mock-match-request-body", "true")
                .setContentType("application/json; charset=utf-8")
                .log(LogDetail.ALL);
        requestSpecification = requestSpecBuilder.build();

        // Note that this will not show the logging. '.log(LogDetail.ALL)' Doesn't work.
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder()
                .expectStatusCode(HTTP_Status_code_OK)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL);

        responseSpecification = responseSpecBuilder.build();

        simplePoJo = new SimplePoJo("value1", "value2");
    }

    @Test
    public void testPostRequestPayloadSimplePoJo() {

        given()
                .log().all()
                .body(simplePoJo)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HTTP_Status_code_OK)
                .body("key1", is(equalTo(simplePoJo.getKey1())),
                        "key2", is(equalTo(simplePoJo.getKey2())));
    }

    /**
     * Ch. 123. Simple POJO: De-serialization.
     * In this example the JSON response will be de-serialized, using RestAssured.as(SimplePoJo.class).
     * This implies that for the response a (separate) class has been defined with the corresponding variables.
     * The respsonse, in JSON, will be de-serialized by adding the JSON content to a Java response class.
     *
     * @throws JsonProcessingException
     */
    @Test
    public void testtestPostRequestPayloadSimplePoJoDeserialization() throws JsonProcessingException {

        SimplePoJo simplePoJoResponse = given()
                .log().all()
                .body(simplePoJo)
                .when()
                .post()
                .then()
                .extract()
                .response()
                .as(SimplePoJo.class);

        ObjectMapper objectMapper = new ObjectMapper();
        String serializedSimplePoJoString = objectMapper.writeValueAsString(simplePoJoResponse);
        String simplePoJoString = objectMapper.writeValueAsString(simplePoJo);

        assertThat(objectMapper.readTree(serializedSimplePoJoString),
                is(equalTo(objectMapper.readTree(simplePoJoString)))
        );
    }

}
