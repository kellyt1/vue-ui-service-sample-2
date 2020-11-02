#! /bin/bash
# 03-add-lambda-permission.sh

source env.sh

APIGATEWAYID=$(aws apigateway get-rest-apis --output text --query "items[?name=='${APPNAME}-api'].id")
SOURCEARN=arn:aws:execute-api:${REGION}:${ACCOUNT}:${APIGATEWAYID}/*/*/*

ACTION=lambda:InvokeFunction
PRINCIPAL=apigateway.amazonaws.com
STATEMENTID=${APPNAME}-AllowExecutionFromAPIGateway

export POLICY=$(aws lambda get-policy --function-name ${APPNAME} --output text --query "Policy")

echo "API Gateway ID              " ${APIGATEWAYID}
echo "Source ARN                  " ${SOURCEARN}
echo "Statement ID                " ${STATEMENTID}
echo "Policy                      " ${POLICY}

if [[ "$POLICY" == *"$STATEMENTID"* ]]; then
    echo "The Lambda Invoke Permission Already Exists - Skipping"
else
    aws lambda add-permission --function-name ${APPNAME} --action ${ACTION} --principal ${PRINCIPAL} --source-arn ${SOURCEARN} --statement-id ${STATEMENTID}
fi

