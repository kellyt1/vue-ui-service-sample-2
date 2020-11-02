FROM openjdk:8u191-jre-alpine3.8
COPY target/vacancy-express-service-*.jar service.jar
EXPOSE 5000
CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar service.jar