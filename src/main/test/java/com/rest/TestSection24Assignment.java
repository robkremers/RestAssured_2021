package com.rest;

import com.rest.entities.Address;
import com.rest.entities.Geo;
import com.rest.entities.User;
import com.rest.entities.WorkspaceRoot;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.responseSpecification;
import static org.hamcrest.Matchers.*;

@Slf4j
public class TestSection24Assignment {

    /**
     * Local parameters
     */
    private final int HTTP_Status_code_OK = 200;
    private final int HTTP_Status_code_Created = 201;

    Response response;

    @BeforeClass
    public void init() {
        baseURI = "https://jsonplaceholder.typicode.com";
        basePath = "/users";

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder
                .setContentType("application/json; charset=utf-8")
                .log(LogDetail.ALL);
        requestSpecification = requestSpecBuilder.build();

        // Note that this will not show the logging. '.log(LogDetail.ALL)' Doesn't work.
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder()
                .expectStatusCode(HTTP_Status_code_Created)
                .expectContentType("application/json; charset=utf-8")
                .log(LogDetail.ALL);

        responseSpecification = responseSpecBuilder.build();
    }

    @Test
    public void testPostRequestPayloadPojo() {

        Geo geo = new Geo();
        geo.setLat("-37.3159");
        geo.setLng("81.1496");

        Address address = new Address();
        address.setStreet("Kulas Light");
        address.setSuite("Apt. 556");
        address.setCity("Gwenborough");
        address.setZipcode("92998-3874");
        address.setGeo(geo);

        User user = new User();
        user.setName("Leanne Graham");
        user.setUsername("Bret");
        user.setEmail("Sincere@april.biz");
        user.setAddress(address);

        given()
                .body(user)
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .body("id", is(not(emptyOrNullString())))
        ;

    }

}
