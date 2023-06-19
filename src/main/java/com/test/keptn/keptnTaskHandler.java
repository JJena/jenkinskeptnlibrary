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
import static com.mimecast.keptn.utils.jsonBeautify.jsonBeautify;

import static com.mimecast.keptn.utils.extractJsonField.extractFieldFromResponse;

public class keptnTaskHandler {

    private static final String keptnEndpoint = "http://10.7.12.37:8081";
    //private static final String xToken = "OobO0fxIY6Kgwf7gzbmq6kyJUfWAgFar7EOts6bL1mm9A";
    private static final String xToken = System.getenv("env.xToken");;
    private static Object keptnContext = "";
    private static Object Score = "";
    private static Object Results = "";
    private static final String keptnContextPath = "keptnContext";
    private static final String scorePath = "events..data..evaluation.score";
    private static final String resultPath = "events..data..evaluation.result";

    public static void main(String[] args) throws InterruptedException {

        try {
            // URL to send the POST request to
            String url = keptnEndpoint+"/api/controlPlane/v1/project/loadtest/stage/loadtest-branch/service/test-service/evaluation";

            // JSON payload for the POST request
            String jsonPayload = "{\n" +
                    "  \"start\": \"2023-06-14T19:47:21.086208886Z\",\n" +
                    "  \"timeframe\": \"5m\"\n" +
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

            if (responseEntity != null) {
                // Extract the response body as a string
                String responseBody = EntityUtils.toString(responseEntity);
                System.out.println("Response: " + responseBody);

                // Extract the desired field from the response
                // Modify this code according to your JSON structure
                // Here, we assume the response is in JSON and has a field named "result"
                keptnContext = extractFieldFromResponse(responseBody, keptnContextPath);
                System.out.println("Trigger Evaluation|keptnContext: " + keptnContext);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread.sleep(3000);

        try {

            // URL to send the POST request to
            String url = keptnEndpoint+"/api/mongodb-datastore/event?keptnContext=" + keptnContext.toString() + "&type=sh.keptn.event.evaluation.finished";

            // Create HttpClient
            HttpClient httpClient = HttpClientBuilder.create().build();

            // Create HttpPost request with URL
            HttpGet httpGet = new HttpGet(url);

            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("x-token", xToken);

            // Send the GET request
            HttpResponse response = httpClient.execute(httpGet);

            // Get the response entity
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                // Extract the response body as a string
                String responseBody = EntityUtils.toString(responseEntity);
                String responseBodyBeautified = jsonBeautify(responseBody);
                System.out.println("Response: " + responseBodyBeautified);

                // Extract the desired field from the response
                // Modify this code according to your JSON structure
                // Here, we assume the response is in JSON and has a field named "result"
                Object score = extractFieldFromResponse(responseBody, scorePath);
                System.out.println("Get Score|score: " + score);
                Object result = extractFieldFromResponse(responseBody, resultPath);
                System.out.println("Get result|result: " + result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




}
