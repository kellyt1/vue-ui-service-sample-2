
Making a Release Build:
2. in /pom.xml rev the version number

4. in /src/main/resources/application.yml redact any credentials (database)

5. build the source code using /gradlew build -x test
===>mvn clean package -DskipTests
6. build the assembly and deploy to Nexus using /mvn assembly:single deploy

