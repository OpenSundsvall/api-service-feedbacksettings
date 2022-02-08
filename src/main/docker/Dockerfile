FROM maven:3.8.4-openjdk-17-slim AS build
COPY . /code
WORKDIR /code

# Necessary in order to run on Jenkins. Comment out when running on local machine without access to nexus.
COPY settings.xml /root/.m2/settings.xml

## Build
RUN mvn clean package -DskipTests

FROM openjdk:17.0.2-slim
ENV TZ="Europe/Stockholm"

COPY --from=build /code/target/*runner.jar ./app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

EXPOSE 9090
