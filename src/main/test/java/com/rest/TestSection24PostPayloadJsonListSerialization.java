package com.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
 * Ch. 117. Serialize List to JSON Array using Jackson.
 *
 * See for earlier info: class Part092TestPostPayloadAsJsonArray.java.
 *
 * Ch. 119.Serialize Jackson Array Node to JSON Array.
 * Now a Jackson object will be created directly instead of translating a HashMap or List.
 *
 * The following will be translated:
 * Json body in the mock:
 * [
 *     { "id": "5001", "type": "None" },
 *     { "id": "5002", "type": "Glazed" }
 * ]
 *
 */
@Slf4j
public class TestSection24PostPayloadJsonListSerialization {

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
        basePath = "/post";

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder
                .setBaseUri(baseURI)
                .addHeader("x-mock-match-request-body", "true")
//                .setContentType(ContentType.JSON)
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

    @Test
    public void testPostRequestPayloadJsonArrayAsList() throws JsonProcessingException {

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
         * Explicit serialization of the JSON using Jackson.
         * This is only done in order to show that Jackson works.
         * Normally we will use it implicitly, as has been done in the earlier example.
         */
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonListString = objectMapper.writeValueAsString(jsonList);

        log.info("********************");
        log.info(jsonListString);
        log.info("********************");

        given()
                .body(jsonListString)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .body("message", is(equalTo("Success")));
    }

    /**
     * Ch. 119.Serialize Jackson Array Node to JSON Array.
     * Now a Jackson object will be created directly instead of translating a HashMap or List.
     */
    @Test
    public void testPostRequestPayloadJsonArrayAsObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        ArrayNode arrayNode = new ObjectMapper().createArrayNode();

        ObjectNode objectNode5001 = objectMapper.createObjectNode();
        objectNode5001.put("id", "5001");
        objectNode5001.put("type", "None");

        ObjectNode objectNode5002 = objectMapper.createObjectNode();
        objectNode5002.put("id", "5002");
        objectNode5002.put("type", "Glazed");

        arrayNode.add(objectNode5001);
        arrayNode.add(objectNode5002);

        given()
                .body(arrayNode)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .body("message", is(equalTo("Success")));
    }
}
