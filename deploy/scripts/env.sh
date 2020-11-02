#! /usr/bin/bash
# env.sh
# set application specific items
export APPNAME=hepdataportal-service-stack
export WEB_SUBDOMIAN=hepdataportalws
export STACK_NAME="hepdataportal-service-stack"
ACCOUNT_NONPROD=938619397650
ACCOUNT_PROD=100582527228
CERT_ARN_NONPROD="arn:aws:acm:us-east-1:938619397650:certificate/4fe467c4-09e0-4115-b91c-819505c63b26"
CERT_ARN_PROD="arn:aws:acm:us-east-1:100582527228:certificate/fb3bc89c-0ebf-4b88-ba29-b91797fa202d"
HOSTED_ZONE_BASE=health.state.mn.us
HOSTED_ZONE_NONPROD=nonprod.${HOSTED_ZONE_BASE}
HOSTED_ZONE_PROD=web.${HOSTED_ZONE_BASE}
# S3 Bucket to store configuration files.
BUCKET_NAME_NONPROD="hepdataportal-mdh-nonprod-sam"
BUCKET_NAME_PROD="mdh-deploy-serverless-prod"
# Cache Database name.
export DB_NAME="hepdataportalrds"
#VPC id
VPCID_NONPROD="vpc-13a55d7a"
VPCID_PROD=""
# Subnet id list
SUBNETID_LIST_NONPROD="subnet-ef864d86,subnet-c9ffe5b1"
SUBNETID_LIST_PROD=""
# Default RDS Security Group
DEFAULT_RDS_SECURITY_NONPROD="sg-8ec751e5"
DEFAULT_RDS_SECURITY_PROD=""
# Keycloak settings for use by the API Gateway Authorizer.
KEYCLOAK_KEY_PROD="{\"kid\":\"vccyNrSdr8LJWkq6KBJhUSCTynNq87eLE6FdrqLnMzE\",\"kty\":\"RSA\",\"alg\":\"RS256\",\"use\":\"sig\",\"n\":\"pKc9tfNln35jWOZSZ2RUSBqe60GuqZm25VjM_5H28-UM8fJDcosw6DAy0oPIDccl-_dF3HDyaWNV_5X0__d9hzW1Lq2PMYMT9y3AbD5KLWq-WKTmioEYQaVMMQ9H_sNOX_9yc4K-6rs-MvWw2WzL5I5EMuJPvaea1VDeJFDuMkUn-lN5QMWw1Mb9HvlgtPDRU4q3flnPxb5RwYVZyD2Ett7jLIPSABoY9bslAMOXQX4QuPB4NQxIupo_ETjcRzgtZjph5K6g0slOlb30iqnG_Jp6dupcGm0EVp9p3QWIGBPiAAI5B0f1-NUUIz7ZruL7XuIZyPHUpOrmRI_ZqMF_EQ\",\"e\":\"AQAB\"}"
KEYCLOAK_KEY_NONPROD="{\"kid\":\"5RXlGlQH7WsjGoTfuDRfK7KdYZqcpBX5L2FYzuaW-5E\",\"kty\":\"RSA\",\"alg\":\"RS256\",\"use\":\"sig\",\"n\":\"nigEP-ilBtuQENFq77wsukNQDO6viTYb-Pii9JLbPQ4OlEq02u5WJsUMLDeXiTS26aVG8cb2y5rnlr8rcTOhTPeBkEuWxOGbiSVcHa-0eZGnkpVVi-hnImhzLqzRKYnfWxDUbymJbKdydGVfaDFOECC-dLrRLUn6Usuc_VjSp4uN5IM1iUXEgVodXaxsx4X8iufra3oH0kYv32_kMMd7CA_RkuUOCmSC3MjiUnnb3A6OX96Tot53UiZe4lLnseviILdJGkxuJ-M7z7KJ5mdFr_yUIeQGExFkk9cGAdVfyquy-IS4yJTMyiOaWp6iYyjALsyCWZJRZSAMFofKXHgqFQ\",\"e\":\"AQAB\"}"
export KEYCLOAK_CLIENT="HEPDataPortal"
# Application AC2CODE
export AC2CODE="3GAES"
export OWNER="EH"
ROLE_REQUIRED=ADFS-AgencyRoleAdministratorsRole
REGION_REQUIRED=us-east-2
# set template specific items
export TEMPLATEFILE=template.yml
# get account id and region
export ACCOUNT=$(aws sts get-caller-identity --output text --query 'Account')
export ARN=$(aws sts get-caller-identity --output text --query 'Arn')
ARN_TRIMED=${ARN%/*}
export ROLE=${ARN_TRIMED#*/}
export REGION=$(aws configure get region)
# set environmented based on account id
if [ "$ACCOUNT" = "${ACCOUNT_NONPROD}" ]; then
        echo "Setting Nonprod Environment Variables"
        export ENVIRONMENT=nonprod
        export BUCKET_NAME=$BUCKET_NAME_NONPROD
        export CERT_ARN=$CERT_ARN_NONPROD
        export HOSTED_ZONE=$HOSTED_ZONE_NONPROD.
        export FULL_DOMAIN_NAME=$WEB_SUBDOMIAN.$HOSTED_ZONE_NONPROD
        export DATA_BUCKET_NAME=$DATA_BUCKET_NAME_NONPROD
        export VPCID=$VPCID_NONPROD
        export SUBNETID_LIST=$SUBNETID_LIST_NONPROD
        export DEFAULT_RDS_SECURITY=$DEFAULT_RDS_SECURITY_NONPROD
        export KEYCLOAK_KEY=$KEYCLOAK_KEY_NONPROD
elif [ "$ACCOUNT" = "${ACCOUNT_PROD}" ]; then
        echo "Setting Prod Environment Variables"
        export ENVIRONMENT=prod
        export BUCKET_NAME=$BUCKET_NAME_PROD
        export CERT_ARN=$CERT_ARN_PROD
        export HOSTED_ZONE=$HOSTED_ZONE_PROD.
        export FULL_DOMAIN_NAME=$WEB_SUBDOMIAN.$HOSTED_ZONE_PROD
        export VPCID=$VPCID_PROD
        export SUBNETID_LIST=$SUBNETID_LIST_PROD
        export DEFAULT_RDS_SECURITY=$DEFAULT_RDS_SECURITY_PROD
        export KEYCLOAK_KEY=$KEYCLOAK_KEY_PROD
else
        export ENVIRONMENT=unknown
        echo "The environment is not known."
        echo "Do you need to login? (awsauth.py)"
        exit 1;
fi
# check for cert settings
if [ "$CERT_ARN" = "unknown" ]; then
        echo "Please set the certificate ARN for this environment (${ENVIRONMENT})."
        exit 2;
fi
# check role
if [ "$ROLE" != "${ROLE_REQUIRED}" ]; then
        echo "Please authenticate with role ${ROLE_REQUIRED} (current role ${ROLE})."
        exit 3;
fi
# check region
if [ "$REGION" != "${REGION_REQUIRED}" ]; then
        echo "The region is not ${REGION_REQUIRED}."
        echo "Are you sure you want to create stuff here?"
        exit 4;
fi
echo "Environment                 " ${ENVIRONMENT}
echo "Account                     " ${ACCOUNT}
echo "Region                      " ${REGION}
echo "Stack Name                  " ${STACK_NAME}
echo "Bucket Name                 " ${BUCKET_NAME}
echo "Keycloak Client             " ${KEYCLOAK_CLIENT}
echo "WEB Subdomain               " ${WEB_SUBDOMIAN}
echo "Hosted Zone                 " ${HOSTED_ZONE}
echo "Cert ARN                    " ${CERT_ARN}
echo "Domain Name                 " ${FULL_DOMAIN_NAME}
