# syntax=docker/dockerfile:1.6
FROM eclipse-temurin:21 AS deps
WORKDIR /code
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

# Copy only Maven wrapper + .mvn + every pom.xml from the build context,
# without enumerating modules (requires BuildKit).
RUN --mount=type=bind,source=.,target=/src,ro \
    bash -ceu ' \
      mkdir -p /code; \
      cd /src; \
      # copy Maven wrapper if present
      if [ -f mvnw ]; then cp mvnw /code/; fi; \
      if [ -d .mvn ]; then mkdir -p /code/.mvn && cp -R .mvn/. /code/.mvn/; fi; \
      # copy all pom.xml preserving directory structure
      while IFS= read -r -d \"\" p; do \
        mkdir -p /code/"$(dirname "$p")"; \
        cp "$p" /code/"$p"; \
      done < <(find . -name pom.xml -type f -print0); \
    '

# Warm up the local Maven repo for all modules (deps + plugins).
RUN --mount=type=cache,target=/root/.m2 \
    bash -ceu ' \
      if [ -x /code/mvnw ]; then MVN=/code/mvnw; else MVN=mvn; fi; \
      cd /code; \
      "$MVN" -B -q -DskipTests -DincludePlugins=true dependency:go-offline \
    '

FROM eclipse-temurin:21 AS builder
WORKDIR /code
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

# Build-time DB args (needed by jooq codegen)
ARG DB_HOST=db
ARG DB_PORT=3306
ARG DB_DATABASE=steve
ARG DB_USER=steve
ARG DB_PASSWORD=steve
ARG PORT=8180
# dockerize version (to wait for DB)
ARG DOCKERIZE_VERSION=v0.19.0

# Now copy the whole project sources
COPY . /code

# Install dockerize to wait for DB
RUN curl -sfL "https://github.com/powerman/dockerize/releases/download/${DOCKERIZE_VERSION}/dockerize-$(uname -s)-$(uname -m)" \
    | install /dev/stdin /usr/local/bin/dockerize

# Wait for DB, then build
# TODO should only skip integration tests using a real database and/or testcontainers
RUN --mount=type=cache,target=/root/.m2 \
    dockerize -wait tcp://${DB_HOST}:${DB_PORT} -timeout 60s && \
    ./mvnw -B clean package \
      -PuseRealDatabase \
      -DskipTests \
      -Ddb.jdbc.url="jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_DATABASE}?useSSL=true&serverTimezone=UTC" \
      -Ddb.schema="${DB_DATABASE}" \
      -Ddb.user="${DB_USER}" \
      -Ddb.password="${DB_PASSWORD}" \
      -Dprofile=prod \
      -Dappender=CONSOLE

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

ARG DB_HOST=db
ARG DB_PORT=3306
ARG DB_DATABASE=steve
ARG DB_USER=steve
ARG DB_PASSWORD=steve
ARG PORT=8180

ENV JAVA_TOOL_OPTIONS="-Dserver.host=0.0.0.0 -Dhttp.port=${PORT} -Ddb.jdbc.url='jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_DATABASE}?useSSL=true&serverTimezone=UTC' -Ddb.schema=${DB_DATABASE} -Ddb.user=${DB_USER} -Ddb.password=${DB_PASSWORD} -Djdk.tls.client.protocols='TLSv1,TLSv1.1,TLSv1.2' -Dserver.gzip.enabled=false"

RUN addgroup --system app && adduser --system --ingroup app app

COPY --from=builder --chown=app:app /code/steve/target/steve.jar /app/
COPY --from=builder --chown=app:app /code/steve/target/libs/ /app/libs/

USER app

EXPOSE ${PORT}
EXPOSE 8443

CMD ["sh", "-c", "exec java -XX:MaxRAMPercentage=85 -jar /app/steve.jar"]
