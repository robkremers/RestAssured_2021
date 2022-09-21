package com.rest.utilities;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import java.io.*;

public class FileReading {

    private static String HOME = System.getProperty("user.home");

    public static File returnFile(String relativeFilePath) {
        return new File(relativeFilePath);
    }

    public static String readFromFile(String relativeFilePath) throws IOException {
        FileInputStream fis = new FileInputStream(relativeFilePath);
        String data = IOUtils.toString(fis, "UTF-8");
        fis.close();

        return data;
    }

    /**
     * https://www.baeldung.com/reading-file-in-java
     * Helper method in order to read from an input stream.
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    /**
     * https://www.baeldung.com/reading-file-in-java
     * http://tutorials.jenkov.com/java-io/fileinputstream.html
     *
     * Does not work.: inputStream = null.
     *
     * @throws IOException
     */
//    @Test
//    public void testLoadFileUsingClassLoader() throws IOException {
//        ClassLoader classLoader = getClass().getClassLoader();
//        InputStream inputStream = classLoader.getResourceAsStream("src/test/resources/createPostWorkspacePayload.json");
//        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
//        String content = "";
//        while( bufferedInputStream.read() != -1) {
//            content += bufferedInputStream.read() + "\n";
//        }
//        bufferedInputStream.close();
//        inputStream.close();
//        log.info(content);
//    }
}
