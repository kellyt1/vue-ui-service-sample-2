#!/usr/bin/bash
set -e
source env.sh

if [ "$ENVIRONMENT" = "nonprod" ]; then
    echo "In nonprod build and deploy is allowed."
else
    echo "Not in nonprod build and deploy is not allowed, Please use deploy only.";
    exit;
fi

(cd ../../ && mvn clean package)

source deploy.sh "../../target/hepdataportal-service-0.0.1.jar"
