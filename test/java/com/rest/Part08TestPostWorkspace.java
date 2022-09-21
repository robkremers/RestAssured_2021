package com.rest;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Ch. 85. POST - BDD Style.
 *
 * In order to execute the delete tests firstly a workspace needs to be created and the id
 * needs to be added.
 */
@Slf4j
public class Part08TestPostWorkspace {

    /**
     * Properties defined in postman.
     */
    private final String X_API_KEY = "X-Api-Key";
    private final String X_API_KEY_VALUE = "<X_API_KEY_VALUE>";

    String workspaceId = "825b9a30-0360-440f-82a7-dffe72448aca";

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

        /**
         * For some reason .log(logDetail.ALL) does not work.
         * Therefore it is necessary to add .log().all() to the RestAssured.given()....get().
         * See below.
         */
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder()
                .expectStatusCode(HTTP_Status_code_OK)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL);

        // A non-standard <> RestAssured.responseSpecification would be given to get().spec().
        // Otherwise the following RestAssured.responseSpecification will be used under the hood of .get() / .post().
//        responseSpecification = responseSpecBuilder.build();

        // Alternatively setup. Works nicely too.
        responseSpecification = RestAssured.expect()
                .statusCode(HTTP_Status_code_OK)
                .contentType(ContentType.JSON);
        log.info("blalba");
    }

    /**
     * BDD-style
     */
    @Test(enabled = true)
    public void testValidatePostRequestBDDStyle() {

        given()
                .body(payload)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .body("workspace.name", is(equalTo("MyFifthWorkspace"))
                        , "workspace.id", matchesPattern("^[a-z0-9-]{36}$"));
    }

    /**
     * Now a different setup is used.
     * This ans the 2nd setup of the responseSpecification is Quiz 11. Question 1.
     */
    @Test
    public void testValidatePostRequestBDDStyle2() {
        given()
                .body(payload)
                .when()
                .post()
                .then().spec(responseSpecification)
                .log().all()
                .assertThat()
                .body("workspace.name", is(equalTo("MyFifthWorkspace")));
    }

    /**
     * Quiz question 2:
     * Does this work?
     * NO: because put("(workspaceId)"). --> should use curly braces instead of round braces.
     */
    @Test(enabled = false)
    public void testQuizQuestion2() {
        String workspaceId = "cd8832a4-0778-4683-a2ee-97a3f17d2ee7";
        String payload = "{\n" +
                "    \"workspace\": {\n" +
                "        \"name\": \"newWorkspaceName\",\n" +
                "        \"type\": \"personal\",\n" +
                "        \"description\": \"this is created by Rest Assured\"\n" +
                "    }\n" +
                "}";
        given().
                body(payload).
                pathParam("workspaceId", workspaceId).
                when().
                put("(workspaceId)").
                then().
                log().all().
                assertThat().
                body("workspace.name", equalTo("newWorkspaceName"),
                        "workspace.id", matchesPattern("^[a-z0-9-]{36}$"),
                        "workspace.id", equalTo(workspaceId));;
    }

    /**
     * Ch. 86. POST - non-BDD-style.
     * RestAssured.given() could also have been used. The difference is only syntactical.
     * See the API documentation.
     * <p>
     * In the current setup no response logging will be created.
     * There are ways around this.
     */
    @Test(enabled = false)
    public void testValidatePostRequestNonBDDStyle() {
        Response response = with()
                .body(payload)
                .post();
        assertThat(response.path("workspace.name"), is(equalTo("MyFifthWorkspace")));
        assertThat(response.path("workspace.id"), matchesPattern("^[a-z0-9-]{36}$"));
    }

    /**
     * Ch. 87. PUT BDD-style.
     * The workspace.id is taken from GET {{baseUrl}}/workspaces/ in Postman.
     * Note:
     * The workspace.description is not part of the response so can not be used for assertions :-)
     */
    @Test(enabled = true)
    public void testValidatePutRequestBDDStyle() {
        String payload =
                "{\n" +
                        "    \"workspace\": {\n" +
                        "        \"name\": \"MySixthWorkspace\",\n" +
                        "        \"type\": \"personal\",\n" +
                        "        \"description\": \"Test for Ch. 86. POST - non-BDD-style version 2.\"\n" +
                        "    }\n" +
                        "}";

        given()
                .body(payload)
                .when()
                .put(workspaceId)
                .then()
                .log().all()
                .assertThat()
                .body("workspace.name", is(equalTo("MySixthWorkspace"))
                        , "workspace.id", matchesPattern("^[a-z0-9-]{36}$")
                        , "workspace.id", is(equalTo(workspaceId))
                )
        ;
        /** Alternatively you can pass the workspaceId as a parameter.
         * Note that RestAssured.basePath has already been set and the parameter will be concatenated:
         * /workspaces/{workspaceId}
         **/
        given()
                .body(payload)
                .pathParam("workspaceId", workspaceId)
                .when()
                .put("{workspaceId}")
                .then()
                .log().all()
                .assertThat()
                .body("workspace.name", is(equalTo("MySixthWorkspace"))
                        , "workspace.id", matchesPattern("^[a-z0-9-]{36}$")
                        , "workspace.id", is(equalTo(workspaceId))
                )
        ;
    }

    /**
     * Ch. 87. PUT BDD-style.
     * The workspace.id is taken from GET {{baseUrl}}/workspaces/ in Postman.
     * Note:
     * The workspace.description is not part of the response so can not be used for assertions :-)
     */
    @Test(enabled = false)
    public void testValidatePutRequestNonBDDStyle() {
        String workspaceId = "825b9a30-0360-440f-82a7-dffe72448aca";
        String payload =
                "{\n" +
                        "    \"workspace\": {\n" +
                        "        \"name\": \"MySixthWorkspace\",\n" +
                        "        \"type\": \"personal\",\n" +
                        "        \"description\": \"Test for Ch. 86. POST - non-BDD-style version 2.\"\n" +
                        "    }\n" +
                        "}";

        // Using the workspaceId directly as a header parameter for .put().
        Response response = with()
                .body(payload)
                .put(workspaceId);
        assertThat(response.path("workspace.name"), is(equalTo("MySixthWorkspace")));
        assertThat(response.path("workspace.id"), matchesPattern("^[a-z0-9-]{36}$"));
        assertThat(response.path("workspace.id"), Matchers.equalTo(workspaceId));

        // Define a parameter and use an indicator that this parameter should be used for .put().
        response = with()
                .body(payload)
                .pathParam("workspaceId", workspaceId)
                .put("{workspaceId}");
        assertThat(response.path("workspace.name"), is(equalTo("MySixthWorkspace")));
        assertThat(response.path("workspace.id"), matchesPattern("^[a-z0-9-]{36}$"));
        assertThat(response.path("workspace.id"), Matchers.equalTo(workspaceId));
    }

    /**
     * Ch. 88. DELETE.
     */
    @Test(enabled = false)
    public void testValidateDeleteRequestBDDStyle() {
        String workspaceId = "825b9a30-0360-440f-82a7-dffe72448aca";

        // BDD-style
        given()
                .when()
                .delete(workspaceId)
                .then()
                .log().all()
        .assertThat()
        .body("workspace.id", is(equalTo(workspaceId))
        , "workspace.id", matchesPattern("^[a-z0-9-]{36}$"))
        ;

        // Non-BDD-style; using a parameter.
        response = with()
                .body(payload)
                .pathParam("workspaceId", workspaceId)
                .delete("{workspaceId}");
        assertThat(response.path("workspace.name"), is(equalTo("MySixthWorkspace")));
        assertThat(response.path("workspace.id"), matchesPattern("^[a-z0-9-]{36}$"));
        assertThat(response.path("workspace.id"), Matchers.equalTo(workspaceId));

    }
}
