#!/usr/bin/bash
set -e
source env.sh

SRC="${1:?Missing jar file}"
if [[ -f "$SRC" ]]; then
    echo "Found jar archive $SRC"
else
    echo "Src archive $SRC does not exist."
    exit;
fi
#AUTH="${2:?Missing Authorizer zip file}"
#if [[ -f "$AUTH" ]]; then
#    echo "Found authorizer archive $AUTH"
#else
#    echo "Authorizer archive $AUTH does not exist."
#    exit;
#fi

SRC_HASH=($(md5sum $SRC))
#AUTH_HASH=($(md5sum $AUTH))

SRC_JAR=$SRC_HASH.jar
#AUTH_ZIP=$AUTH_HASH.zip

# Upload deployment jar to s3
aws s3 cp $SRC s3://$BUCKET_NAME/$SRC_JAR
# Upload authorizer zip to s3
#aws s3 cp $AUTH s3://$BUCKET_NAME/$AUTH_ZIP


aws cloudformation package \
    --template-file ../cloudformation/$TEMPLATEFILE \
    --output-template-file template.packaged.yaml \
    --s3-bucket $BUCKET_NAME

aws cloudformation deploy \
    --template-file template.packaged.yaml \
    --stack-name $STACK_NAME \
    --parameter-overrides "S3Bucket=$BUCKET_NAME" "SrcJar=$SRC_JAR" \
        "AcmCertificateArn=$CERT_ARN" \
        "HostedZoneName=$HOSTED_ZONE" "FullDomainName=$FULL_DOMAIN_NAME" \
        "AppName=$STACK_NAME" "KeycloakKey=$KEYCLOAK_KEY" \
        "KeycloakClient=$KEYCLOAK_CLIENT" "DBName=$DB_NAME" \
        "SubnetIdList=$SUBNETID_LIST" "VpcId=$VPCID" \
        "DefaultRdsSecurityGroup=$DEFAULT_RDS_SECURITY" \
    --tags "owner=$OWNER" "ac2code=$AC2CODE" \
    --capabilities CAPABILITY_NAMED_IAM \
    --s3-bucket $BUCKET_NAME
