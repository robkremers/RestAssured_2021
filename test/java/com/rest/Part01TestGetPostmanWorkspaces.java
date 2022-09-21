package com.rest;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Because I have set baseUri and basePath I don't have to set given.baseUri() and get() for all workspaces.
 * For a specific workspace the .get("<workspace ID>") will append to baseURI/basePath/.
 */
@Slf4j
public class Part01TestGetPostmanWorkspaces {

    /**
     * Properties defined in postman.
     */
    private final String X_API_KEY = "X-Api-Key";
    private final String X_API_KEY_VALUE = "<X_API_KEY_VALUE>";
    private final int HTTP_Status_code_OK = 200;
    private final int HTTP_Status_code_Created = 201;

    @BeforeClass
    public static void init() {
        baseURI = "https://api.postman.com";
        basePath = "/workspaces";
    }

    @Test
    public void testBasicRestAssuredSetup() {
        RestAssured.given()
                .when()
                .then();
    }

    /**
     * .log().all(): will log the entire response to the console.
     * The .log().all() should be called before .assertThat()
     * because in case of failure the .log().all() would not be executed.
     */
    @Test
    public void testGetPostmanStatusCodeOK() {
        given()
//                .baseUri("https://api.postman.com")
                .header("X-Api-Key", X_API_KEY_VALUE)
                .when()
                .get()
                .then()
                .log().all()
                .contentType(ContentType.JSON)
                .assertThat()
                .statusCode(HTTP_Status_code_OK)
        ;
    }

    @Test
    /**
     * The following will return the body of the response.
     * This is handy when finetuning the following tests.
     * Note that this can also be done via .log().body().
     */
    public void testGetPostManBodyMatch() {

        Response response = given()
//                .baseUri("https://api.postman.com/")
                .header("X-Api-Key", X_API_KEY_VALUE)
                .when()
                .contentType(ContentType.JSON)
                .get("/7fcf506e-2dfa-4530-ba94-78a0a315a86b");

        log.info("Response:");
        log.info(response.getBody().prettyPrint());
    }

    /**
     * For my account multiple workspaces exist in Postman.
     * For the specific workspace 'Postman Essentials Workspace' the name is checked.
     * In order to do this we do a GET with /baseUri/basePath/<workspace ID>.
     */
    @Test
    public void testWorkspaceName() {
        given()
//                .baseUri("https://api.postman.com/")
                .header("X-Api-Key", X_API_KEY_VALUE)
                .when()
                .get("/7fcf506e-2dfa-4530-ba94-78a0a315a86b")
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HTTP_Status_code_OK)
                .body("workspace.name", is(equalTo("Postman Essentials Workspace")));

    }

    /**
     * The last assertion with regards to "workspaces[0].name" is of course dangerous:
     * The order of elements in a JSON files can change without warning.
     * But it serves the purpose for now.
     * <p>
     * With regards to the implementation of multiple assertions in one test:
     * If more than one test fails they will be mentioned separately.
     * Unless of course the first assertion makes it impossible to continue.
     * e.g. of the test on contentType fails no other asssertions will be carried out.
     * If two tests in .body() fail two separate assertionErrors will be shown.
     * --> It remains best practice to have one assertion per test method.
     */
    @Test
    public void testAllWorkspaceNames() {
        given()
                .header("X-Api-Key", X_API_KEY_VALUE)
                .when()
                .get()
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HTTP_Status_code_OK)
                .body("workspaces.name", hasItems("Yet another Workspace", "My Workspace", "Postman Essentials Workspace", "Team Workspace")
                        , "workspaces.type", hasItems("personal", "personal", "team")
                        , "workspaces[0].name", is(equalTo("Yet another Workspace"))
                );
    }

    @Test
    public void testNumberOfWorkspaces() {
        given()
                .header("X-Api-Key", X_API_KEY_VALUE)
                .when()
                .get()
                .then()
                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HTTP_Status_code_OK)
                .body("workspaces.size()", equalTo(4))
        ;
    }

    /**
     * Ch. 56. Automate GET Request - Extract Response.
     * <p>
     * Example of the use of interface io.restassured.response.ValidatableResponseOptions
     * - method: extract().
     * Extract values from the response or return the response instance itself.
     * This is useful for example if you want to use values from the response in sequent requests.
     * <p>
     * This is a different approach compared to e.g. the previous test setup in testNumberOfWorkspaces()
     * where the response is tested using groovy-style extraction.
     */
    @Test
    public void testResponseExtraction() {
        Response response = given()
                .header("X-Api-Key", X_API_KEY_VALUE)
                .when()
                .get()
                .then()
//                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HTTP_Status_code_OK)
                .extract()
                .response();

        log.info("repsonse: {}");
        log.info(response.asPrettyString());

    }

    /**
     * Ch. 57 Automate GET Request - Extract Single Field.
     * <p>
     * Determine the correct JsonPath using: http://groovy-playground.appspot.com/
     */
    @Test
    public void testExtractSingleFieldd() {
        Response response = given()
                .header("X-Api-Key", X_API_KEY_VALUE)
                .when()
                .get()
                .then()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HTTP_Status_code_OK)
                .extract()
                .response();

        JsonPath jsonPath = new JsonPath(response.asString());

        /**
         * Using JsonPath the content of the response body can be retrieved in various ways.
         */
        // Option 1.
        log.info("Option 1: workspace name = " + response.path("workspaces[2].name"));

        // Option 2.
        log.info("Option 2: workspace name = " + jsonPath.get("workspaces[2].name"));

        // Option 3.
        log.info("Option 3: workspace name = " + JsonPath.from(response.asString()).get("workspaces[2].name"));

        // Option 4.
        String workspaceName = given()
                .header("X-Api-Key", X_API_KEY_VALUE)
                .when()
                .get()
                .then()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HTTP_Status_code_OK)
                .extract()
                .response().path("workspaces[2].name");

        log.info("Option 4: workspace name = " + workspaceName);
    }

    /**
     * Ch. 58. Hamcrest Assertion on Extracted Response.
     * Resources:
     * - http://hamcrest.org/JavaHamcrest/javadoc/2.1/
     */
    @Test
    public void testHamcrestAssertOnExtractedResponse() {
        String workspaceName = given()
                .header("X-Api-Key", X_API_KEY_VALUE)
                .when()
                .get()
                .then()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HTTP_Status_code_OK)
                .extract()
                .response().path("workspaces[2].name");

        // Hamcrest
        assertThat(workspaceName, equalTo("Postman Essentials Workspace"));

        // TestNG: now I can add a specific message for the situation that a failure occurs.
        Assert.assertEquals(workspaceName, "Postman Essentials Workspace", "The workspace name was incorrect.");
    }

    @Test
    public void testTestNGAssertOnExtractedResponse() {
        String workspaceName = given()
                .header("X-Api-Key", X_API_KEY_VALUE)
                .when()
                .get()
                .then()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HTTP_Status_code_OK)
                .extract()
                .response().path("workspaces[2].name");

        // TestNG: now I can add a specific message for the situation that a failure occurs.
        Assert.assertEquals(workspaceName, "Postman Essentials Workspace", "The workspace name was incorrect.");
    }

    /**
     * Ch. 60 Hamcrest CollectionRoot Matches - Part 1.
     * contains() -> Check all elements are in a collection and in a strict order
     * If the order is is not correct an error will occur:
     * <p>
     * java.lang.AssertionError: 1 expectation failed.
     * JSON path workspaces.name doesn't match.
     * Expected: iterable containing ["Yet another Workspace", "My Workspace", "Team Workspace", "Postman Essentials Workspace"]
     * Actual: <[Yet another Workspace, My Workspace, Postman Essentials Workspace, Team Workspace]>
     */
    @Test
    public void testHamcrestContainsAllWorkspaceNames() {
        given()
                .header("X-Api-Key", X_API_KEY_VALUE)
                .when()
                .get()
                .then()
//                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HTTP_Status_code_OK)
                .body("workspaces.name"
                        , contains("Yet another Workspace", "My Workspace", "Postman Essentials Workspace", "Team Workspace")
                );
    }

    /**
     * Ch. 60 Hamcrest CollectionRoot Matches - Part 1.
     * containsInAnyOrder() -> Check all elements are in a collection and in any order
     * The order is not equal to the actual situation but the test will pass.
     * See:
     * http://groovy-playground.appspot.com/
     * {
     * "workspaces": [
     * {
     * "id": "1b7a0f2c-f29e-47a5-9876-8209a17feb34",
     * "name": "Yet another Workspace",
     * "type": "personal"
     * },
     * {
     * "id": "5a7edc8d-9407-4010-8935-31e851c39449",
     * "name": "My Workspace",
     * "type": "personal"
     * },
     * {
     * "id": "7fcf506e-2dfa-4530-ba94-78a0a315a86b",
     * "name": "Postman Essentials Workspace",
     * "type": "team"
     * },
     * {
     * "id": "efbb8b92-6110-4e3a-8c6e-81e8b511fe9d",
     * "name": "Team Workspace",
     * "type": "team"
     * }
     * ]
     * }
     */
    @Test
    public void testHamcrestContainsInAnyOrderAllWorkspaceNames() {
        given()
                .header("X-Api-Key", X_API_KEY_VALUE)
                .when()
                .get()
                .then()
//                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HTTP_Status_code_OK)
                .body("workspaces.name"
                        , containsInAnyOrder("Yet another Workspace", "My Workspace", "Team Workspace", "Postman Essentials Workspace")
                );
    }

    /**
     * Ch. 61. Hamcresst CollectionRoot Matchers - Part 2.
     * <p>
     * empty() -> Check if collection is empty
     * Should work for all Collections.
     * <p>
     * The last test would fail if active:
     * java.lang.AssertionError: 1 expectation failed.
     * JSON path workspaces.name doesn't match.
     * Expected: every item is a string starting with "Team"
     * Actual: <[Yet another Workspace, My Workspace, Postman Essentials Workspace, Team Workspace]>
     */
    @Test
    public void testHamcrestIsEmptyAllWorkspaces() {
        given()
                .header("X-Api-Key", X_API_KEY_VALUE)
                .when()
                .get()
                .then()
//                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HTTP_Status_code_OK)
                .body("workspaces.name"
                        , is(not(empty()))
                        , "workspaces.name"
                        , is(not(emptyArray()))
                        , "workspaces.name"
                        , hasSize(4)
                        , "workspaces.name"
                        , not(everyItem(startsWith("Team")))
                );
    }

    /**
     * Ch. 62. Hamcrest CollectionRoot Matches - Part 3.
     * <p>
     * Use Hamcrest methods that are applicable for Maps.
     * worspaces[0] -->
     * {id=1b7a0f2c-f29e-47a5-9876-8209a17feb34, name=Yet another Workspace, type=personal}
     * <p>
     * hasKey(K key)
     * Creates a matcher for Maps matching when the examined Map contains at least one key that is equal to the specified key.
     */
    @Test
    public void testHamcrestMappingAllWorkspaces() {
        given()
                .header("X-Api-Key", X_API_KEY_VALUE)
                .when()
                .get()
                .then()
//                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HTTP_Status_code_OK)
                .body("workspaces[0]", hasKey("id")
                        , "workspaces[0]", hasValue("Yet another Workspace")
                        , "workspaces[0]", hasEntry("id", "1b7a0f2c-f29e-47a5-9876-8209a17feb34")
                        , "workspaces[0]", hasValue("personal")
                        , "workspaces[0]", hasEntry("type", "personal")
                        , "workspaces[0]", not(equalTo(Collections.EMPTY_MAP))
                )
        ;
    }

    /**
     * Ch. 63. Hamcrest CollectionRoot Matches - Part 4.
     * <p>
     * Again:
     * contains() checks whether the content is matched exactly.
     */
    @Test
    public void testHamcrestAlloffAllWorkspaces() {
        given()
                .header("X-Api-Key", X_API_KEY_VALUE)
                .when()
                .get()
                .then()
//                .log().all()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HTTP_Status_code_OK)
                .body("workspaces[0].name", allOf(not(startsWith("Team")))
                        , "workspaces[0].name", containsString("Yet another Workspace")
                        , "workspaces[1].name", anyOf(containsString("My Workspace")
                                , containsString("Team Workspace"))
                )
        ;
    }

    /**
     * Ch. 64. Request and Response Logging
     * - the log().all() after .header() ensures that the request is logged.
     * Request method:	GET
     * Request URI:	https://api.postman.com/workspaces
     * Proxy:			<none>
     * Request params:	<none>
     * Query params:	<none>
     * Form params:	    <none>
     * Path params:	    <none>
     * Headers:		    X-Api-Key=<X_API_KEY_VALUE>
     * Accept=*\/*
     * Cookies:		    <none>
     * Multiparts:		<none>
     * Body:			    <none>
     * - the log().all() after .then() ensures that the response is logged.
     * HTTP/1.1 200 OK
     * Access-Control-Allow-Origin: *
     * Content-Encoding: gzip
     * Content-Type: application/json; charset=utf-8
     * Date: Tue, 22 Jun 2021 19:51:24 GMT
     * ETag: W/"17b-t2XRUeIwjakWCZyXHI0xOQr+T1M"
     * Server: nginx
     * Vary: Accept-Encoding
     * x-frame-options: SAMEORIGIN
     * X-RateLimit-Limit: 60
     * X-RateLimit-Remaining: 59
     * X-RateLimit-Reset: 1624391544
     * x-srv-span: v=1;s=f89cd3bc653b3540
     * x-srv-trace: v=1;t=2d45092ab379aa60
     * Content-Length: 234
     * Connection: keep-alive
     * <p>
     * {
     * "workspaces": [
     * {
     * "id": "1b7a0f2c-f29e-47a5-9876-8209a17feb34",
     * "name": "Yet another Workspace",
     * "type": "personal"
     * },
     * {
     * "id": "5a7edc8d-9407-4010-8935-31e851c39449",
     * "name": "My Workspace",
     * "type": "personal"
     * },
     * {
     * "id": "7fcf506e-2dfa-4530-ba94-78a0a315a86b",
     * "name": "Postman Essentials Workspace",
     * "type": "team"
     * },
     * {
     * "id": "efbb8b92-6110-4e3a-8c6e-81e8b511fe9d",
     * "name": "Team Workspace",
     * "type": "team"
     * }
     * ]
     * }
     * <p>
     * Note: this is a GET request without a request body (although this is possible).
     * <p>
     * Note:
     * - After .header() a logging with the following method is used:
     * - io.restassured.specification.Part03TestRequestSpecification RequestLogSpecification log()
     * - After .then() a logging with the following method is used:
     * - io.restassured.response.ValidatableResponseLogSpec<T, R> log()
     */
    @Test
    public void testRequestResponseLogging() {
        given()
                .header("X-Api-Key", X_API_KEY_VALUE)
                .log().all() //io.restassured.specification.Part03TestRequestSpecification RequestLogSpecification log()
//                .log().headers()
//                .log().cookies()
//                .log().body()
                .when()
                .get()
                .then()
                .log().all()    // io.restassured.response.ValidatableResponseLogSpec<T, R> log()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HTTP_Status_code_OK)
        ;
    }

    /**
     * Ch. 64. Log if Error.
     * <p>
     * Note:
     * - After .header() a logging with the following method is used:
     * - io.restassured.specification.Part03TestRequestSpecification RequestLogSpecification log()
     * - This method does NOT allow the use of logging in case of error only.
     * - After .then() a logging with the following method is used:
     * - io.restassured.response.ValidatableResponseLogSpec<T, R> log()
     * - This method does allow the use of logging in case of error only.
     * <p>
     * Result when adding the wrong value for X-Api-Key:
     * HTTP/1.1 401 Unauthorized
     * Access-Control-Allow-Origin: *
     * Content-Type: application/json; charset=utf-8
     * Date: Tue, 22 Jun 2021 20:16:42 GMT
     * ETag: W/"78-WpLmxRlFkKKPPDh/y4B5Gn3NuSs"
     * Server: nginx
     * Vary: Accept-Encoding
     * x-frame-options: SAMEORIGIN
     * X-RateLimit-Limit: 400
     * X-RateLimit-Remaining: 399
     * X-RateLimit-Reset: 1624393062
     * x-srv-span: v=1;s=26f9703a22ff209f
     * x-srv-trace: v=1;t=df0bb4fe39e09c81
     * Content-Length: 120
     * Connection: keep-alive
     * <p>
     * {
     * "error": {
     * "name": "AuthenticationError",
     * "message": "Invalid API Key. Every request requires a valid API Key to be sent."
     * }
     * }
     * <p>
     * java.lang.AssertionError: 1 expectation failed.
     * Expected status code <200> but was <401>.
     * <p>
     * Without .log().ifError() only the following would be shown:
     * <p>
     * java.lang.AssertionError: 1 expectation failed.
     * Expected status code <200> but was <401>.
     */
    @Test
    public void testLogOnlyIfError() {
        given()
                .header("X-Api-Key", X_API_KEY_VALUE)
                .log().all()
                .when()
                .get()
                .then()
//                .log().all()
//                .log().ifError()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HTTP_Status_code_OK)
        ;
    }

    /**
     * Ch. 65. Log if Validation Fails.
     * - For the situation that .statusCode(HTTP_Status_code_OK) fails.
     * <p>
     * - After .header() a logging with the following method is used:
     * - io.restassured.specification.Part03TestRequestSpecification RequestLogSpecification log().ifValidationFails()
     * - After .then() a logging with the following method is used:
     * - io.restassured.response.ValidatableResponseLogSpec<T, R> log().ifValidationFails()
     * <p>
     * --> shows logging only if the http response status is not of the expected type.
     */
    @Test
    public void testLogOnlyIfValidationFails() {
        log.info("Situation in which .log().ifValidationFails() is used after .header() and .then().");
        given()
                .header("X-Api-Key", X_API_KEY_VALUE)
                .log().ifValidationFails()
                .when()
                .get()
                .then()
//                .log().all()
//                .log().ifError()
                .log().ifValidationFails()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HTTP_Status_code_OK)
        ;

        log.info("Improved version using .config() in order to avoid using .log().ifValidationFails() twice.");
        given()
                .header("X-Api-Key", X_API_KEY_VALUE)
                /**
                 * Note that only things known to REST Assured (i.e. the request- and response specifications) will be logged.
                 * If you need to log what's actually sent on the wire refer to the HTTP Client logging docs
                 * or use an external tool such as Wireshark.
                 */
                .config(config.logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
//                .log().ifValidationFails()
                .when()
                .get()
                .then()
//                .log().all()
//                .log().ifError()
//                .log().ifValidationFails()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HTTP_Status_code_OK) // Change this in order to create a validation error.
        ;
    }

    /**
     * Ch. 66. Blacklist Headers.
     *
     * Situation:
     * - Logging is desired, but the header content of "X-Api-Key" needs to be suppressed since it is sensitive.
     *
     * Result of the implementation below:
     * .config(config.logConfig(LogConfig.logConfig().blacklistHeader("X-Api-Key", "Accept")))
     * Headers:		X-Api-Key=[ BLACKLISTED ]
     * 				Accept=[ BLACKLISTED ]
     * Alternatively using:
     * .config(config.logConfig(LogConfig.logConfig().blacklistHeaders(headers)))
     * Headers:		X-Api-Key=[ BLACKLISTED ]
     * 				Accept=[ BLACKLISTED ]
     */
    @Test
    public void testLogsBlacklistHeader() {
        log.info("The use of blacklistHeader() in order to hide the value of specific headers.");

        Set<String> headers = new HashSet<>();
        headers.add("X-Api-Key");
        headers.add("Accept");

        given()
                .header("X-Api-Key", X_API_KEY_VALUE)
//                .config(config.logConfig(LogConfig.logConfig().blacklistHeader("X-Api-Key", "Accept")))
                .config(config.logConfig(LogConfig.logConfig().blacklistHeaders(headers)))
//                .log().all()
                .when()
                .get()
                .then()
                .assertThat()
                .contentType(ContentType.JSON)
                .statusCode(HTTP_Status_code_OK) // Change this in order to create a validation error.
        .log().all()
        ;
    }
}
