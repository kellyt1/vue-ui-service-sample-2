#! /bin/bash
# 01-package-lambda-apigateway.sh

source 00-env.sh

aws cloudformation package --template-file ${TEMPLATEFILE} --s3-bucket ${S3DEPLOYMENTBUCKET} --output-template-file ${PACKAGETEMPLATEFILE}