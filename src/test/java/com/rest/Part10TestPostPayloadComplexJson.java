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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Ch. 95 Send Complex JSON using Map and List.
 *
 */
@Slf4j
public class Part10TestPostPayloadComplexJson {
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
        basePath = "/postComplexJson";

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

//        // Alternatively setup. Works nicely too. Again: here '.log(LogDetail.ALL)' would not work.
//        responseSpecification = RestAssured.expect()
//                .statusCode(HTTP_Status_code_OK)
//                .contentType(ContentType.JSON);
    }

    /**
     * Mock post:
     * {
     * 	"id": "0001",
     * 	"type": "donut",
     * 	"name": "Cake",
     * 	"ppu": 0.55,
     * 	"batters":
     *                {
     * 			"batter":
     * 				[
     *                    { "id": "1001", "type": "Regular" },
     *                    { "id": [5, 9],
     *                         "type": "Chocolate"
     *                     }
     * 				]
     *        },
     * 	"topping":
     * 		[
     *            { "id": "5001", "type": "None" },
     *            { "id": "5002",
     *                 "type": ["test1", "test2"]
     *             }
     * 		]
     * }
     *
     * The logging will show the json object as it has been setup.
     * In case of an error:
     * - Check the setup of the json object.
     * - Take the json object, place it in the Postman mock POST and execute the POST.
     *   --> If this is wrong: fix it.
     *   --> If this is successful: the error is not in the json object.
     */
    @Test
    public void validatePostPayloadComplexJson() {

        // Set up the body, using Map and List, in order to set up the POST body above.

        /** batters **/
        List<Integer> idArrayList = new ArrayList<>(Arrays.asList(5, 9));

        Map<String, Object> batterHashMap1 = new HashMap<>();
        batterHashMap1.put("id", "1001");
        batterHashMap1.put("type", "Regular");

        Map<String, Object> batterHashMap2 = new HashMap<>();
        batterHashMap2.put("id", idArrayList);
        batterHashMap2.put("type", "Chocolate");

        List<Map<String, Object>> batterArrayList = new ArrayList<>();
        batterArrayList.add(batterHashMap1);
        batterArrayList.add(batterHashMap2);

        Map<String, List<Map<String, Object>>> battersHashMap = new HashMap<>();
        battersHashMap.put("batter", batterArrayList);

        /** topping **/
        Map<String, Object> toppingHashMap1 = new HashMap<>();
        toppingHashMap1.put("id", "5001");
        toppingHashMap1.put("type", "None");

        List<String> typeArrayList = new ArrayList(Arrays.asList("test1", "test2"));
        Map<String, Object> toppingHashMap2 = new HashMap<>();
        toppingHashMap2.put("id", "5002");
        toppingHashMap2.put("type", typeArrayList);

        List<Map<String, Object>> toppingArrayList = new ArrayList(Arrays.asList(toppingHashMap1, toppingHashMap2));

        /** Base Json **/
        Map<String, Object> mainHashMap = new HashMap<>();
        mainHashMap.put("id", "0001");
        mainHashMap.put("type", "donut");
        mainHashMap.put("name", "Cake");
        mainHashMap.put("ppu", 0.55);
        mainHashMap.put("batters", battersHashMap);
        mainHashMap.put("topping", toppingArrayList);

        given()
                .body(mainHashMap)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .body("message", is(equalTo("Success")));
    }

}
