#! /bin/bash
# 02-deploy-lambda-apigateway.sh

source 00-env.sh

aws cloudformation deploy --template-file ${PACKAGETEMPLATEFILE} \
    --stack-name ${STACKNAME} \
    --capabilities CAPABILITY_NAMED_IAM \
    --parameter-overrides \
        AppName=${APPNAME} \
        VPCId=${VPC_ID} \
        SubnetIdList=${SUBNETS} \
        HostedZoneName=${HOSTED_ZONE} \
        ApiUrl=${API_SUBDOMIAN}.${HOSTED_ZONE} \
        AcmCertArn=${CERT_ARN}