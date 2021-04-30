package com.myorg;

import org.json.JSONObject;
import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableProps;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.secretsmanager.Secret;
import software.amazon.awscdk.services.secretsmanager.SecretProps;
import software.amazon.awscdk.services.kms.Key;
import software.amazon.awscdk.services.kms.IKey;
import software.amazon.awscdk.services.secretsmanager.SecretStringGenerator;

import java.util.Map;

public class AwsCdkStack extends Stack {
    public AwsCdkStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsCdkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Creation of DynamoDB in stack, configuration is set in tableProps
        TableProps tableProps;
        Attribute partitionKey = Attribute.builder()
                .name("marketHashName")
                .type(AttributeType.STRING)
                .build();
        Attribute sortKey = Attribute.builder()
                .name("assetId")
                .type(AttributeType.STRING)
                .build();
        tableProps = TableProps.builder()
                .tableName("InventoriesTest2")
                .partitionKey(partitionKey)
                .sortKey(sortKey)
                // The default billing mode is Provisioned, can be changed to On-demand, other configurations like
                // Auto Scaling can also be set here
                // .billingMode()
                // .readCapacity()
                // .writeCapacity()
                // The default removal policy is RETAIN, which means that cdk destroy will not attempt to delete
                // the new table, and it will remain in your account until manually deleted. By setting the policy to
                // DESTROY, cdk destroy will delete the table (even if it has data in it)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
        Table dynamodbTable = new Table(this, "items", tableProps);

//        -----------------Lambda Function example-----------------
//        Map<String, String> lambdaEnvMap = new HashMap<>();
//        lambdaEnvMap.put("TABLE_NAME", dynamodbTable.getTableName());
//        lambdaEnvMap.put("PRIMARY_KEY","itemId");
//
//        Function getOneItemFunction = new Function(this, "getOneItemFunction",
//                getLambdaFunctionProps(lambdaEnvMap, "software.amazon.awscdk.examples.lambda.GetOneItem"));
//        Function getAllItemsFunction = new Function(this, "getAllItemsFunction",
//                getLambdaFunctionProps(lambdaEnvMap, "software.amazon.awscdk.examples.lambda.GetAllItems"));
//        Function createItemFunction = new Function(this, "createItemFunction",
//                getLambdaFunctionProps(lambdaEnvMap, "software.amazon.awscdk.examples.lambda.CreateItem"));
//        Function updateItemFunction = new Function(this, "updateItemFunction",
//                getLambdaFunctionProps(lambdaEnvMap, "software.amazon.awscdk.examples.lambda.UpdateItem"));
//        Function deleteItemFunction = new Function(this, "deleteItemFunction",
//                getLambdaFunctionProps(lambdaEnvMap, "software.amazon.awscdk.examples.lambda.DeleteItem"));
//
//        dynamodbTable.grantReadWriteData(getOneItemFunction);
//        dynamodbTable.grantReadWriteData(getAllItemsFunction);
//        dynamodbTable.grantReadWriteData(createItemFunction);
//        dynamodbTable.grantReadWriteData(updateItemFunction);
//        dynamodbTable.grantReadWriteData(deleteItemFunction);

        // Creation of Secret Manager in stack
        // The initial secret values are set in the form of JSON string
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("test", "test");
        String payload = jsonObject.toString();

        // The encryption key used in the Secret is imported by ARN
        IKey iKey = Key.fromKeyArn(this,
                "test",
               "arn:aws:kms:ap-southeast-2:417864917128:key/20d20044-5c0a-4d92-809f-794c3aa70db1");

        // configuration is set in secretProps
        SecretProps secretProps = SecretProps.builder()
                .encryptionKey(iKey)
                .secretName("testName1")
                .removalPolicy(RemovalPolicy.DESTROY)
                .generateSecretString(SecretStringGenerator.builder()
                        .secretStringTemplate(payload)
                        .generateStringKey("test1").
                                build())
                .build();
        Secret secret = new Secret(this, "testSecret", secretProps);
    }

//        -----------------Lambda Function example-----------------
//    private FunctionProps getLambdaFunctionProps(Map<String, String> lambdaEnvMap, String handler) {
//        return FunctionProps.builder()
//                .code(Code.fromAsset("./asset/lambda-1.0.0-jar-with-dependencies.jar"))
//                .handler(handler)
//                .runtime(Runtime.JAVA_8)
//                .environment(lambdaEnvMap)
//                .timeout(Duration.seconds(30))
//                .memorySize(512)
//                .build();
//    }
}
