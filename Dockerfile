FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
ARG SERVICE
RUN mvn clean package -pl ${SERVICE} -am -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
ARG SERVICE
COPY --from=build /app/${SERVICE}/target/*.jar app.jar
EXPOSE 8080 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
