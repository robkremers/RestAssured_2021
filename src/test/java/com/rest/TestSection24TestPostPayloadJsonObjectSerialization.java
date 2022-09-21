package com.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rest.entities.Workspace;
import com.rest.entities.WorkspaceRoot;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.matchesPattern;

/**
 * Ch. 116. Serialize Map to JSON using Jackson.
 */
@Slf4j
public class TestSection24TestPostPayloadJsonObjectSerialization {

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

    Response response;

    @BeforeClass
    public void init() {
        baseURI = "https://api.postman.com";
        basePath = "/workspaces";

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder
                .addHeader(X_API_KEY, X_API_KEY_VALUE)
//                .setContentType(ContentType.JSON)
                .setContentType("application/json; charset=utf-8")
                .log(LogDetail.ALL);
        requestSpecification = requestSpecBuilder.build();

        // Note that this will not show the logging. '.log(LogDetail.ALL)' Doesn't work.
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder()
                .expectStatusCode(HTTP_Status_code_OK)
                .expectContentType("application/json; charset=utf-8")
                .log(LogDetail.ALL);

        responseSpecification = responseSpecBuilder.build();
    }

    /**
     * To be sent via POST:
     * <p>
     * {
     * "workspace": {
     * "name": "MyFifthWorkspace",
     * "type": "personal",
     * "description": "workspace for BDD Style testing the POST HTTP method"
     * }
     * }
     * Response:
     * The response will be like this (the id will of course be different):
     * {
     * "workspace": {
     * "id": "4b1c4696-3b6b-4107-af96-90c84eb51b63",
     * "name": "MyFifthWorkspace"
     * }
     * }
     */
    @Test
    public void testPostRequestPayloadWorkspaceAsMap() {

        Map<String, Object> mainObject = new HashMap<>();

        Map<String, String> nestedObject = new HashMap<>();
        nestedObject.put("name", "MyFifthWorkspace");
        nestedObject.put("type", "personal");
        nestedObject.put("description", "workspace for BDD Style testing the POST HTTP method");

        mainObject.put("workspace", nestedObject);

        /**
         * .body(mainObject) serializes the mainObject using Jackson (see for this pom.xml).
         */
        given()
                .body(mainObject)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .body("workspace.name", is(equalTo("MyFifthWorkspace"))
                        , "workspace.id", matchesPattern("^[a-z0-9-]{36}$"));
    }

    /**
     * In this test Jackson will be used explicitly instead of under the hood.
     */
    @Test
    public void testPostRequestPayloadWorkspaceUseObjectMapper() throws JsonProcessingException {

        Map<String, Object> mainObject = new HashMap<>();

        Map<String, String> nestedObject = new HashMap<>();
        nestedObject.put("name", "MyFifthWorkspace");
        nestedObject.put("type", "personal");
        nestedObject.put("description", "workspace for BDD Style testing the POST HTTP method");

        mainObject.put("workspace", nestedObject);

        /**
         * Explicit serialization of the JSON using Jackson.
         * This is only done in order to show that Jackson works.
         * Normally we will use it implicitly, as has been done in the first test.
         */
        ObjectMapper objectMapper = new ObjectMapper();
        String mainObjectStr = objectMapper.writeValueAsString(mainObject);

        log.info("********************");
        log.info(mainObjectStr);
        log.info("********************");

        given()
                .body(mainObjectStr)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .body("workspace.name", is(equalTo("MyFifthWorkspace"))
                        , "workspace.id", matchesPattern("^[a-z0-9-]{36}$"));
    }

    /**
     * Ch. 118. Serialize Jackson Object Node to JSON Object.
     * <p>
     * Now a Jackson object will be created directly instead of translating a HashMap or List.
     *
     * @throws JsonProcessingException
     */
    @Test
    public void testPostRequestPayloadWorkspaceUseOjectMapperOnly() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode nestedObjectNode = objectMapper.createObjectNode();
        nestedObjectNode.put("name", "MyFifthWorkspace");
        nestedObjectNode.put("type", "personal");
        nestedObjectNode.put("description", "workspace for BDD Style testing the POST HTTP method");

        ObjectNode mainObjectNode = objectMapper.createObjectNode();
        // Note that for adding an ObjectNode instance the method .put() is deprecated.
        mainObjectNode.set("workspace", nestedObjectNode);

        given()
                .body(mainObjectNode)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .body("workspace.name", is(equalTo("MyFifthWorkspace"))
                        , "workspace.id", matchesPattern("^[a-z0-9-]{36}$"));
    }

    /**
     * Ch. 124. Workspace POJO: Serialize and De-serialize.
     * <p>
     * In the following:
     * 1. An instance of WorkspaceRoot will be serialized --> JSON.
     * 2. A response in JSON will be deserialized to another instance of WorkspaceRoot.
     * <p>
     * In this case Jackson will be used implicitly again.
     */
    @Test
    public void testPostRequestPayloadWorkspaceUseOjectMapperSerialization() {

        Workspace workspace = new Workspace("MyFifthWorkspace"
                , "personal"
                , "workspace for BDD Style testing the POST HTTP method"
        );
        WorkspaceRoot workspaceRoot = new WorkspaceRoot(workspace);

        WorkspaceRoot workspaceRootResponse = given()
                .body(workspaceRoot)
                .when()
                .post()
                .then()
                .log().all()
                .extract()
                .as(WorkspaceRoot.class);

        assertThat(workspaceRootResponse.getWorkspace().getName(),
                is(equalTo(workspaceRoot.getWorkspace().getName())));
        assertThat(workspaceRootResponse.getWorkspace().getId(), matchesPattern("^[a-z0-9-]{36}$"));
    }

    /**
     * org.testng.annotations.Type DataProvider
     * Mark a method as supplying data for a test method.
     * The data provider name defaults to method name.
     * The annotated method must return an Object[][] where each Object[] can be assigned
     * the parameter list of the test method. The @Test method that wants to receive data
     * from this DataProvider needs to use a dataProvider name equals to the name of this annotation.
     * <p>
     * The result of the  implementation is that in this case, with two test sets,
     * the method testPostRequestPayloadWorkspaceUseOjectMapperDerialization()
     * will be executed twice.
     *
     * @return
     */
    @DataProvider(name = "workspace")
    public Object[][] getWorkspace() {
        return new Object[][]{
                {"workspace5", "personal", "description"},
                {"workspace6", "team", "description"}
        };
    }

    @Test(dataProvider = "workspace")
    public void testPostRequestPayloadWorkspaceUseOjectMapperDerialization(
              String name
            , String type
            , String description) {

        Workspace workspace = new Workspace(name
                , type
                , description
        );
        Map<String, String> myMap = new HashMap<>();
        workspace.setMyMap(myMap);

        WorkspaceRoot workspaceRoot = new WorkspaceRoot(workspace);

        WorkspaceRoot workspaceRootResponse = given()
                .body(workspaceRoot)
                .when()
                .post()
                .then()
                .log().all()
                .extract()
                .as(WorkspaceRoot.class);

        assertThat(workspaceRootResponse.getWorkspace().getName(),
                is(equalTo(workspaceRoot.getWorkspace().getName())));
        assertThat(workspaceRootResponse.getWorkspace().getId(), matchesPattern("^[a-z0-9-]{36}$"));
    }
}
