# syntax=docker/dockerfile:1.7

# La etapa de build no es Alpine a proposito: el plugin de Vaadin descarga un Node.js enlazado
# contra glibc para construir el frontend de produccion, y en musl no arranca.
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests dependency:go-offline

COPY src ./src
# ~/.vaadin guarda el Node descargado y la cache del frontend entre construcciones.
RUN --mount=type=cache,target=/root/.m2 --mount=type=cache,target=/root/.vaadin mvn -B -DskipTests package

FROM eclipse-temurin:25-jre-alpine

RUN apk add --no-cache curl

WORKDIR /app
COPY --from=build /workspace/target/mto-backoffice-*.jar /app/app.jar
# OpenShift (SCC restricted-v2) arranca el contenedor con un UID aleatorio y el grupo 0; Docker, con
# el 1001, que sin entrada en /etc/passwd tambien es del grupo 0. /app es del grupo 0 y el grupo puede
# lo mismo que el dueno, asi que los dos funcionan igual y ninguno es root.
RUN chgrp -R 0 /app && chmod -R g=u /app

# Sin valores por defecto para KEYCLOAK_ISSUER_URI, KEYCLOAK_CLIENT_SECRET ni MTO_GATEWAY_URL: el
# perfil prod los exige y una imagen no debe llevar dentro a que realm apunta.
ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080 \
    LOGGING_LEVEL_ROOT=INFO \
    JAVA_OPTS=""

EXPOSE 8080

USER 1001
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
