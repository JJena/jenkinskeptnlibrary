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
import java.time.LocalDateTime;

import static com.test.keptn.utils.extractJsonField.extractFieldFromResponse;

public class runTest {
    private static Object keptnContext = "";
    private static Object newTaskState = "started";
    private static final String initialTaskState = "started";
    private static final String runTestPath = "/api/v1/event";
    private static final String getTaskStatusPath = "/api/controlPlane/v1/sequence/loadtest?keptnContext=";
    private static String Url = "";
    private static final String keptnContextPath = "keptnContext";
    private static final String taskStatePath = "states[0].state";
    private static String runTestPayload = "{\n" +
            "  \"data\": {\n" +
            "     \"labels\": {\n" +
            "    },\n" +
            "    \"project\": \"project_name\",\n" +
            "    \"service\": \"service_name\",\n" +
            "    \"stage\": \"stage_name\"\n" +
            "  },\n" +
            "  \"type\": \"sh.keptn.event.stage_name.loadtest-execution.triggered\",\n" +
            "  \"source\": \"bridge\"\n" +
            "}";

    public static String runTest(String keptnEndpoint, String xToken, String project, String service, String stage) throws InterruptedException {

        try {
            // URL to send the POST request to
            Url = keptnEndpoint + runTestPath;

            // Create HttpClient
            HttpClient httpClient = HttpClientBuilder.create().build();

            // Create HttpPost request with URL
            HttpPost httpPost = new HttpPost(Url);

            //Replace the payload placeholders with actual values
            runTestPayload = runTestPayload.replace("project_name",project);
            runTestPayload = runTestPayload.replace("service_name",service);
            runTestPayload = runTestPayload.replace("stage_name",stage);

            // Set the JSON payload as the request entity
            StringEntity entity = new StringEntity(runTestPayload);
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
        Url = keptnEndpoint+getTaskStatusPath+keptnContext;
        // Create HttpClient
        HttpClient httpClient = HttpClientBuilder.create().build();

        // Create Http Get request with URL
        HttpGet httpGet = new HttpGet(Url);

        httpGet.setHeader("Content-Type", "application/json");
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("x-token", xToken);

        //Keep checking status till the new state is "started"
        while(newTaskState.toString().equals(initialTaskState)) {
            // Send the GET request
            HttpResponse response = httpClient.execute(httpGet);
            // Get the response entity
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                // Extract the response body as a string
                String responseBody = EntityUtils.toString(responseEntity);

                // Extract the desired field from the response
                // Modify this code according to your JSON structure
                // Here, we assume the response is in JSON and has a field named "result"
                newTaskState = extractFieldFromResponse(responseBody, taskStatePath);
                System.out.println(LocalDateTime.now() +":checkTaskCompletion|Test execution in Progress|taskState: "+newTaskState);
            }
            Thread.sleep(10000);
        }
    } catch (IOException | InterruptedException e) {
        e.printStackTrace();
        }
    }
}
