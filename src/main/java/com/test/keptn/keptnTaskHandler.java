package com.test.keptn;


public class keptnTaskHandler {

    private static final String keptnEndpoint = "http://10.7.12.37:8081";
    private static String xToken = "";
    private static String testMode = "";
    private static String project = "";
    private static String service = "";
    private static String stage = "";
    private static String sloFile = "";
    private static String evaluationDurationMinutes = "";

    public static void main(String[] args) throws InterruptedException {

        if (args.length > 0)
        {
            xToken = args[0];
            testMode = args[1];
            project = args[2];
            service = args[3];
            stage = args[4];
            sloFile = args[5];
            evaluationDurationMinutes = args[6];
        }
    switch (testMode){
        case "Test_And_Evaluate":
            String keptnContext = runTest.runTest(keptnEndpoint,xToken,project,service,stage);
            Thread.sleep(3000);
            runTest.checkTestCompletion(keptnEndpoint,xToken,keptnContext);
            Thread.sleep(3000);
            runEvaluate.evaluateTest(keptnEndpoint,xToken,project,service,stage,sloFile,evaluationDurationMinutes);
            break;
        case "Evaluate":
            runEvaluate.evaluateTest(keptnEndpoint,xToken,project,service,stage,sloFile,evaluationDurationMinutes);
            break;
        default:
            throw new RuntimeException("Test Mode not supported: "+testMode);
        }
    }
}
