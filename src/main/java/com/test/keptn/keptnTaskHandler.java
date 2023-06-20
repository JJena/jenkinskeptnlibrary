package com.test.keptn;


import java.io.UnsupportedEncodingException;

public class keptnTaskHandler {

    private static final String keptnEndpoint = "http://10.7.12.37:8081";
    private static String xToken = "";
    private static String testMode = "";

    public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException {

        if (args.length > 0)
        {
            xToken = args[0];
            testMode = args[1];
        }
    switch (testMode){
        case "Test_And_Evaluate":
            String keptnContext = runTest.runTest(keptnEndpoint,xToken);
            runTest.checkTestCompletion(keptnEndpoint,xToken,keptnContext);
            break;
        case "Evaluate":
            runEvaluate evaluateTest = new runEvaluate();
            evaluateTest.runEvaluate(keptnEndpoint,xToken);
            break;
        default:
            throw new RuntimeException("Test Mode not supported: "+testMode);
        }
    }
}
