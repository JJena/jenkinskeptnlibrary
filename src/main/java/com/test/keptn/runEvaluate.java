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

public class runEvaluate {
    private static Object keptnContext = "";
    private static Object Score = "";
    private static Object Results = "";
    private static final String keptnContextPath = "keptnContext";
    private static final String evaluatePath = "/api/v1/event";
    private static String getResultPath = "/api/mongodb-datastore/event?keptnContext=placeholder&type=sh.keptn.event.evaluation.finished";
    private static final String scorePath = "events..data..evaluation.score";
    private static final String resultPath = "events..data..evaluation.result";
    private static String Url = "";
    private static String evaluateTestPayload = "{\n" +
            "  \"data\": {\n" +
            "    \"labels\": {\n" +
            "       \"slofilename\": \"slofile_name\" \n" +
            "    },\n" +
            "    \"evaluation\": {\n" +
            "      \"timeframe\": \"evaluationDurationMinutes\"\n" +
            "    },\n" +
            "    \"project\": \"project_name\",\n" +
            "    \"service\": \"service_name\",\n" +
            "    \"stage\": \"stage_name\"\n" +
            "  },\n" +
            "  \"source\": \"https://github.com/keptn/keptn/api\",\n" +
            "  \"type\": \"sh.keptn.event.stage_name.evaluation.triggered\"\n" +
            "}";

    public static void evaluateTest(String keptnEndpoint, String xToken, String project, String service, String stage, String sloFile, String evaluationDurationMinutes) throws InterruptedException {

        //Trigger Evaluation
        try {
            // URL to send the POST request to
            Url = keptnEndpoint + evaluatePath;

            // Create HttpClient
            HttpClient httpClient = HttpClientBuilder.create().build();

            // Create HttpPost request with URL
            HttpPost httpPost = new HttpPost(Url);

            //Replace json payload placeholders with actual values
            evaluateTestPayload = evaluateTestPayload.replace("project_name",project);
            evaluateTestPayload = evaluateTestPayload.replace("service_name",service);
            evaluateTestPayload = evaluateTestPayload.replace("stage_name",stage);
            evaluateTestPayload = evaluateTestPayload.replace("evaluationDurationMinutes",evaluationDurationMinutes);

            // Set the JSON payload as the request entity
            StringEntity entity = new StringEntity(evaluateTestPayload);
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
                System.out.println("Response: " + responseBody);

                // Extract the desired field from the response
                // Modify this code according to your JSON structure
                // Here, we assume the response is in JSON and has a field named "result"
                keptnContext = extractFieldFromResponse(responseBody, keptnContextPath);
                System.out.println("runEvaluate|keptnContext: " + keptnContext);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread.sleep(3000);

        //Get results of the evaluation
        try {
            getResultPath = getResultPath.replace("placeholder",keptnContext.toString());
            // URL to send the GET request to
            Url = keptnEndpoint + getResultPath;

            // Create HttpClient
            HttpClient httpClient = HttpClientBuilder.create().build();

            // Create HttpPost request with URL
            HttpGet httpGet = new HttpGet(Url);

            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("x-token", xToken);

            // Send the GET request
            HttpResponse response = httpClient.execute(httpGet);
            assert response.getStatusLine().getStatusCode() < 400;

            // Get the response entity
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                // Extract the response body as a string
                String responseBody = EntityUtils.toString(responseEntity);
                String responseBodyBeautified = jsonBeautify(responseBody);

                // Extract the desired field from the response
                // Modify this code according to your JSON structure
                // Here, we assume the response is in JSON and has a field named "result"
                Score = extractFieldFromResponse(responseBody, scorePath);
                Results = extractFieldFromResponse(responseBody, resultPath);
                System.out.println("runEvaluate|score: " + Score +"result: "+Results);
                System.out.println("runEvaluate| Detailed result:\n" + responseBodyBeautified);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
