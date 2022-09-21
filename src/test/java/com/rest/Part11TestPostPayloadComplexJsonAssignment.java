package com.rest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.responseSpecification;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Assignment 1. Assignment - Automate Complex JSON.
 */
@Slf4j
public class Part11TestPostPayloadComplexJsonAssignment {

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
        basePath = "/postComplexJsonAssignment1";

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
    }

    /**
     * {
     *   "colors": [
     *     {
     *       "color": "black",
     *       "category": "hue",
     *       "type": "primary",
     *       "code": {
     *         "rgba": [
     *           255,
     *           255,
     *           255,
     *           1
     *         ],
     *         "hex": "#000"
     *       }
     *     },
     *     {
     *       "color": "white",
     *       "category": "value",
     *       "code": {
     *         "rgba": [
     *           0,
     *           0,
     *           0,
     *           1
     *         ],
     *         "hex": "#FFF"
     *       }
     *     }
     *   ]
     * }
     */
    @Test
    public void validatePostPayloadComplexJson() {

        /** Color Black **/
        List<Integer> listRgba1 = new ArrayList<>(Arrays.asList(255, 255, 255, 1));

        Map<String, Object> codeMap1 = new HashMap<>();
        codeMap1.put("rgba", listRgba1);
        codeMap1.put("hex", "#000");

        Map<String, Object> colorBlackMap = new HashMap<>();
        colorBlackMap.put("color", "black");
        colorBlackMap.put("category", "hue");
        colorBlackMap.put("type", "primary");
        colorBlackMap.put("code", codeMap1);

        /** Color White **/
        List<Integer> listRgba2 = new ArrayList<>(Arrays.asList(0, 0, 0, 1));

        Map<String, Object> codeMap2 = new HashMap<>();
        codeMap2.put("rgba", listRgba2);
        codeMap2.put("hex", "#FFF");

        Map<String, Object> colorWhiteMap = new HashMap<>();
        colorWhiteMap.put("color", "white");
        colorWhiteMap.put("category", "value");
        colorWhiteMap.put("code", codeMap2);

        /** List of Colors **/
        List<Map<String, Object>> colors = new ArrayList<>();
        colors.add(colorBlackMap);
        colors.add(colorWhiteMap);

        /** Map of Colors **/

        Map<String, List<Map<String, Object>>> colorsMap = new HashMap();
        colorsMap.put("colors", colors);

        given()
                .body(colorsMap)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .body("message", is(equalTo("Success")));
    }

}
