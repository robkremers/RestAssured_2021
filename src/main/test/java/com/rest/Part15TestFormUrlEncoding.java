package com.rest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.responseSpecification;

@Slf4j
public class Part15TestFormUrlEncoding {

    /**
     * Local parameters
     */
    private final int HTTP_Status_code_OK = 200;
    private final int HTTP_Status_code_Created = 201;

    Response response;

    @BeforeClass
    public void init() {
        baseURI = "http://postman-echo.com";
        basePath = "/post";

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();

        requestSpecBuilder
                .setBaseUri(baseURI)
                .setContentType("application/x-www-form-urlencoded; charset=utf-8")
                .log(LogDetail.ALL);
        requestSpecification = requestSpecBuilder.build();

        // Note that this will not show the logging. '.log(LogDetail.ALL)' Doesn't work.
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder()
                .expectStatusCode(HTTP_Status_code_OK)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL);

        responseSpecification = responseSpecBuilder.build();
    }

    /**
     * Ch. 106. Automate Form URL Encoded Request Payload.
     *
     * I have added the init() method in order to standardize the functionality.
     *
     * The From parameters are sent as follows (see Request logging):
     * Form params:	key1=value1
     * 				key2=value2
     */
    @Test
    public void testPostRequestFormUrlEncoded() {

        given()
                // Here RestAssured is instructed not to use the default characterset (charset=ISO-8859-1)
                // This would result in an HTTP 500 Internal Server Error
                // Now the content-type will be:
                // Content-Type=application/x-www-form-urlencoded (See request logging).
                // Alternatively you can specify the Content-Type in the RestAssured.requestSpecification.
                // Has been added to the io.restassured.builder.RequestSpecBuilder.
//                .config(config().encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .formParam("key1", "value1")
                .formParam("key2", "value2")
                .log().all()
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HTTP_Status_code_OK);
    }

}
