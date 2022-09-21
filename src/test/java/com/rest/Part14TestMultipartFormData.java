package com.rest;

import com.rest.utilities.FileReading;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.filter.log.LogDetail;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

/**
 * Resources:
 * https://github.com/rest-assured/rest-assured/wiki/Usage#multi-part-from-data
 *
 */
@Slf4j
public class Part14TestMultipartFormData {

    /**
     * Local parameters
     */
    private final int HTTP_Status_code_OK = 200;
    private final int HTTP_Status_code_Created = 201;

    @BeforeClass
    public void init() {
        // The base Url of the Mock server that has been set up in Postman.
        baseURI = "http://postman-echo.com";
        basePath = "/post";
    }

    /**
     * Ch. 102. Automate Multipart Form Data
     * The body can either contain form-data or a body.
     * Since in the following method .multiPart comes first .body() does not have an effect.
     * Visible in the logging:
     * POST:
     * Headers:		Content-Type = multipart/form-data
     * Multiparts:		------------
     * 				Content-Disposition: form-data; name = form-data-key1; filename = file
     * 				Content-Type: text/plain
     *
     * 				form-data-value1
     * Response:
     * ..
     * "form": {
     *         "form-data-key1": "form-data-value1"
     *     },
     * ..
     *
     * In the case of Multipart form data, the data is sent in parts.
     */
    @Test
    public void testMultipartForm_data() {
        Map<String, String> queryParametermap = new HashMap<>();
        queryParametermap.put("foo1", "bar1");
        queryParametermap.put("foo2", "bar2");

        String body = "{\n" +
                        "    \"message\": \"test\"\n" +
                        "}";

        given()
//                .queryParams(queryParametermap)
                .multiPart("form-data-key1", "form-data-value1")
                .multiPart("form-data-key2", "form-data-value2")
//                .body(body)
                .log().all()
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HTTP_Status_code_OK);
    }

    /**
     * Ch. 103. Upload File.
     * So the file src/test/resources/multipart-test.txt has been uploaded.
     * This is visible in the logging of both request and response.
     *
     * Compare this with the example in Postman:
     * postman-echo.com/post
     *
     */
    @Test
    public void testUploadFileMultipartFormData() {

        String relativePath = "src/test/resources/multipart-test.txt";
        String attributes = "{\"name\":\"multipart-test.txt\",\"parent\":{\"id\":\"123456\"}}";

        File testFile = FileReading.returnFile(relativePath);

        given()
                .multiPart("file", testFile)
                .multiPart("attributes", attributes, "application/json")
                .log().all()
                .when()
                .post()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HTTP_Status_code_OK);
    }

    /**
     * Ch. 104. Download file.
     * The file ApiDemos-debug.apk will be downloaded from
     * https://github.com/appium/appium/blob/master/sample-code/apps/ApiDemos-debug.apk
     * - download a file (click on button Download)
     *
     * https://github.com/appium/appium/blob/master/sample-code/apps/ApiDemos-debug.apk
     * - download a file (click on button Download)
     *
     * - F12: Developer tool
     *   - Tab Network
     *     - Refresh
     *     - Headers
     *       - file apiDemos-debug.apk
     *         -
     *           Request URL: https://github.com/appium/appium/blob/master/sample-code/apps/ApiDemos-debug.apk
     *           Request Method: GET
     *           Status Code: 200 # can also be 304
     *           Remote Address: 140.82.121.3:443
     *           Referrer Policy: strict-origin-when-cross-origin
     * After downloading the file ApiDemos-debug.apk will be present in the root of the project.
     * (because that's what has been specified in the method.
     *
     * @throws IOException
     */
    @Test
    public void testDownloadFile() throws IOException {

        baseURI = "https://raw.githubusercontent.com";
        basePath = "/appium/appium/master/sample-code/apps/ApiDemos-debug.apk";

        byte[] bytes = given()
                .log().all()
                .when()
                .get()
                .then()
                .log().all()
                .extract()
                .response().asByteArray();

        try(OutputStream outputStream = new FileOutputStream(new File("ApiDemos-debug.apk"));) {
            outputStream.write(bytes);
        }
    }

    /**
     * Ch. 104. Download file.
     *
     * https://raw.githubusercontent.com/appium/appium/master/sample-code/apps/ApiDemos-debug.apk
     * is a file.
     * It is an example file that can be downloaded freely.
     * (but any online available file will do).
     * So the example in this method shows that RestAssured can download a file and
     * the result can be tested.
     * In the example below the file will be read to a byte array via an InputStream.
     * This byte array will be written to a file via an OutputStream.
     *
     * https://www.baeldung.com/java-inputstream-to-outputstream
     *
     * @throws IOException
     */
    @Test
    public void testDownloadFile2() throws IOException {
        baseURI = "https://raw.githubusercontent.com";
        basePath = "/appium/appium/master/sample-code/apps/ApiDemos-debug.apk";

        InputStream inputStream = given()
//                .baseUri("https://raw.githubusercontent.com")
                .log().all()
                .when()
                .get()
                .then()
                .log().all()
                .extract()
                .response().asInputStream();

        try(OutputStream outputStream = new FileOutputStream(new File("ApiDemos-debug.apk"));) {
            byte[] bytes = new byte[inputStream.available()];

            // Reads some number of bytes from the input stream and stores them into the buffer array b.
            inputStream.read(bytes);

            outputStream.write(bytes);
        }
    }
}
