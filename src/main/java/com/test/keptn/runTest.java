package com.test.keptn;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import static com.test.keptn.utils.extractJsonField.extractFieldFromResponse;
import static com.test.keptn.utils.jsonBeautify.jsonBeautify;

public class runTest {
    private static Object keptnContext = "";
    private static Object Score = "";
    private static Object Results = "";
    private static Object TaskState = "";
    private static final String runTestPath = "/api/v1/event";
    private static final String getTaskStatusPath = "/api/controlPlane/v1/sequence/loadtest?keptnContext=";
    private static final String keptnContextPath = "keptnContext";
    private static final String scorePath = "events..data..evaluation.score";
    private static final String resultPath = "events..data..evaluation.result";
    private static final String taskStatePath = "states..[?(@.state=='finished')]";

    public static String runTest(String keptnEndpoint, String xToken) throws InterruptedException {

        try {
            // URL to send the POST request to
            String url = keptnEndpoint + runTestPath;

            // JSON payload for the POST request
            String jsonPayload = "{\n" +
                    "  \"data\": {\n" +
                    "     \"labels\": {\n" +
                    "    },\n" +
                    "    \"project\": \"loadtest\",\n" +
                    "    \"service\": \"test-service\",\n" +
                    "    \"stage\": \"loadtest-branch\",\n" +
                    "    \"temporaryData\": {\n" +
                    "      \"distributor\": {\n" +
                    "        \"subscriptionID\": \"\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"type\": \"sh.keptn.event.loadtest-branch.loadtest-execution.triggered\",\n" +
                    "  \"source\": \"bridge\"\n" +
                    "}";

            // Create HttpClient
            HttpClient httpClient = HttpClientBuilder.create().build();

            // Create HttpPost request with URL
            HttpPost httpPost = new HttpPost(url);

            // Set the JSON payload as the request entity
            StringEntity entity = new StringEntity(jsonPayload);
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("x-token", xToken);

            // Send the POST request
            HttpResponse response = httpClient.execute(httpPost);

            // Get the response entity
            HttpEntity responseEntity = response.getEntity();
            assert response.getStatusLine().getStatusCode() < 400;

            if (responseEntity != null) {
                // Extract the response body as a string
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("runTest|Test triggerred successfully|Response: " + responseBody);

                // Extract the desired field from the response
                // Modify this code according to your JSON structure
                // Here, we assume the response is in JSON and has a field named "result"
                keptnContext = extractFieldFromResponse(responseBody, keptnContextPath);
                System.out.println(" runTest|keptnContext: " + keptnContext);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keptnContext.toString();
    }
    public static void checkTestCompletion(String keptnEndpoint, String xToken, String keptnContext) {

    try{
        String url = keptnEndpoint+getTaskStatusPath+keptnContext;
        // Create HttpClient
        HttpClient httpClient = HttpClientBuilder.create().build();

        // Create Http Get request with URL
        HttpGet httpGet = new HttpGet(url);

        httpGet.setHeader("Content-Type", "application/json");
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("x-token", xToken);

        String taskStatus="";
        while(taskStatus.equals("")) {
            // Send the GET request
            HttpResponse response = httpClient.execute(httpGet);
            // Get the response entity
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                // Extract the response body as a string
                String responseBody = EntityUtils.toString(responseEntity);
                String responseBodyBeautified = jsonBeautify(responseBody);
                System.out.println("checkTaskCompletion|Test execution in Progress");

                // Extract the desired field from the response
                // Modify this code according to your JSON structure
                // Here, we assume the response is in JSON and has a field named "result"
                Object taskState = extractFieldFromResponse(responseBody, taskStatePath);
                System.out.println("checkTaskCompletion|taskState: " + taskState);
                taskStatus = taskState.toString();
            }
            Thread.sleep(10000);
        }
    } catch (IOException | InterruptedException e) {
        e.printStackTrace();
        }
    }
}
