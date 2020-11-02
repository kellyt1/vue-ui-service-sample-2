#! /bin/bash
# do-all.sh

./01-package-lambda-apigateway.sh
./02-deploy-lambda-apigateway.sh
./03-add-lambda-permission.sh