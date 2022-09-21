package com.rest;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static com.rest.utilities.FileReading.readFromFile;

/**
 * Section 16: Rest Assured - Send Request Payload Multiple Ways
 * <p>
 * Ch. 89. Send as a File.
 */
@Slf4j
public class Part091TestRequestPayloadMultipleWays {
    /**
     * Properties defined in postman.
     */
    private final String X_API_KEY = "X-Api-Key";
    private final String X_API_KEY_VALUE = "<X_API_KEY_VALUE>";

    /**
     * Local parameters
     */
    private final int HTTP_Status_code_OK = 200;
    private final int HTTP_Status_code_Created = 201;

    String payload =
            "{\n" +
                    "    \"workspace\": {\n" +
                    "        \"name\": \"MyFifthWorkspace\",\n" +
                    "        \"type\": \"personal\",\n" +
                    "        \"description\": \"workspace for BDD Style testing the POST HTTP method\"\n" +
                    "    }\n" +
                    "}";

    Response response;

    @BeforeClass
    public void init() {
        baseURI = "https://api.postman.com";
        basePath = "/workspaces";

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder
                .addHeader("X-Api-Key", X_API_KEY_VALUE)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL);
        requestSpecification = requestSpecBuilder.build();

//        /**
//         * For some reason .log(logDetail.ALL) does not work.
//         * Therefore it is necessary to add .log().all() to the RestAssured.given()....get().
//         * See below.
//         */
//        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder()
//                .expectStatusCode(HTTP_Status_code_OK)
//                .expectContentType(ContentType.JSON)
//                .log(LogDetail.ALL);

        // Alternatively setup. Works nicely too.
        responseSpecification = RestAssured.expect()
                .statusCode(HTTP_Status_code_OK)
                .contentType(ContentType.JSON);
    }

    /**
     * Ch. 89. Send as a File.
     *
     * @throws IOException
     */
    @Test
    public void testValidatePostRequestFromFile() throws IOException {

        String jsonFile = "src/test/resources/createPostWorkspacePayload.json";
        String expectedFileContent =
                "{\n" +
                        "    \"workspace\": {\n" +
                        "        \"name\": \"MyFifthWorkspace\",\n" +
                        "        \"type\": \"personal\",\n" +
                        "        \"description\": \"workspace for BDD Style testing the POST HTTP method\"\n" +
                        "    }\n" +
                        "}";

        String filePayload = readFromFile(jsonFile);

        log.info("filePayload = \n{}", filePayload);

        given()
                .body(payload)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
        .body("workspace.name", is(equalTo("MyFifthWorkspace"))
        , "workspace.id", matchesPattern("^[a-z0-9-]{36}$"))
        ;
    }

    /**
     * Ch. 89. Send as a File.
     * given().body() resolves a relative path!!!!
     *
     * @throws IOException
     */
    @Test
    public void testValidatePostRequestFromFile2() throws IOException {

        String jsonFile = "src/test/resources/createPostWorkspacePayload.json";

        File file = new File(jsonFile);

        String filePayload = readFromFile(jsonFile);

        log.info("filePayload = \n{}", filePayload);

        given()
                .body(file)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .body("workspace.name", is(equalTo("MySixthWorkspace"))
                        , "workspace.id", matchesPattern("^[a-z0-9-]{36}$"))
        ;
    }

    /**
     * Ch. 90. Send Nested JSON Object as a Map.
     *
     * POST Body:
     * {
     *     "workspace": {
     *         "name": "MyFifthWorkspace",
     *         "type": "personal",
     *         "description": "workspace for BDD Style testing the POST HTTP method"
     *     }
     * }
     *
     * Now additionally Jackson-databind is needed. See pom.xml
     */
    @Test
    public void testValidatePostRequestPayloadAsMap() {
        Map<String, Object> mainObject = new HashMap<>();
        Map<String, String> nestedObject= new HashMap<>();
        nestedObject.put("name", "MySeventhWorkspace");
        nestedObject.put("type", "personal");
        nestedObject.put("description", "workspace for BDD Style testing the POST HTTP method");
        mainObject.put("workspace", nestedObject);

        given()
                .body(mainObject)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .body("workspace.name", is(equalTo("MySeventhWorkspace"))
                        , "workspace.id", matchesPattern("^[a-z0-9-]{36}$"));

    }

    @Test
    public void testValidatePostRequestPayloadJsonArrayAsList() {

        given()
                .body("")
                .when()
                .post()
                .then()
                .log().all()
                ;
    }
}
