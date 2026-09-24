# Multi-stage build: compila con Maven+JDK, ejecuta con JRE liviano.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY shared-kernel/pom.xml shared-kernel/pom.xml
COPY services/customer-service/pom.xml services/customer-service/pom.xml
COPY services/geo-catalog-service/pom.xml services/geo-catalog-service/pom.xml

COPY shared-kernel/src shared-kernel/src
COPY services/customer-service/src services/customer-service/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -pl services/customer-service -am -DskipTests package

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
RUN useradd --system --create-home appuser
COPY --from=build /workspace/services/customer-service/target/customer-service.jar app.jar
USER appuser
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
