package com.rest;

import com.rest.entities.WorkspaceRoot;
import com.rest.entities.collection.*;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.responseSpecification;

@Slf4j
public class TestSection26PostPayloadComplexPoJo {
    /**
     * Local parameters
     */
    private final int HTTP_Status_code_OK = 200;
    private final int HTTP_Status_code_Created = 201;

    /**
     * Postman collections parameters
     */
    private final String X_API_KEY = "X-Api-Key";
    private final String X_API_KEY_VALUE = "PMAK-60be82a8f120f500350530bd-5564f633c07f09b9472b464b32bbb43cc3";

    Response response;

    @BeforeClass
    public void init() {
        baseURI = "https://api.postman.com";
        basePath = "/collections";

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder
                .setBaseUri(baseURI)
                .addHeader("X-Api-Key", X_API_KEY_VALUE)
                .setContentType("application/json; charset=utf-8")
                .log(LogDetail.ALL);
        requestSpecification = requestSpecBuilder.build();

        // Note that this will not show the logging. '.log(LogDetail.ALL)' Doesn't work.
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder()
//                .expectStatusCode(HTTP_Status_code_OK)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL);

        responseSpecification = responseSpecBuilder.build();
    }

    @Test
    public void testPostRequestComplexJson() {

        Header header = new Header("Content-Type", "application/json; charset=utf-8");
        List<Header> headerList = new ArrayList<>();
        headerList.add(header);
        Body body = new Body("raw", "{\"data\": \"123\"}");
        Request request = new Request("https://postman-echo.com/post"
                , "POST"
                , headerList
                , body
                , "This is a sample POST Request");

        RequestRoot requestRoot = new RequestRoot("Sample POST Request", request);
        List<RequestRoot> requestRootList = new ArrayList<>();
        requestRootList.add(requestRoot);

        Folder folder = new Folder("This is a folder", requestRootList);
        List<Folder> folderList = new ArrayList<>();
        folderList.add(folder);

        Info info = new Info("Sample Collection"
        , "This is just a sample collection."
        , "https://schema.getpostman.com/json/collection/v2.1.0/collection.json");

        Collection collection = new Collection(info, folderList);
        CollectionRoot collectionRoot = new CollectionRoot(collection);

        given()
                .body(collectionRoot)
                .when()
                .post()
                .then();
    }

}
