FROM eclipse-temurin:21-jdk
MAINTAINER daynnnnn

ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

ARG DB_HOST
ARG DB_USERNAME
ARG DB_PASSWORD
ARG DB_DATABASE
ARG DB_PORT

WORKDIR /code

ADD /src /code/src
ADD /website /code/website
ADD /pom.xml /code/pom.xml

RUN ./mvnw clean package \
      -DskipTests \
      -Dhttp.port=8180 \
      -Dserver.host="0.0.0.0" \
      -Ddb.ip="${DB_HOST}" \
      -Ddb.port=${DB_PORT} \
      -Ddb.schema="${DB_DATABASE}" \
      -Ddb.user="${DB_USERNAME}" \
      -Ddb.password="${DB_PASSWORD}" \
      -Dserver.gzip.enabled=false \
      -Dappender="CONSOLE" \
      -Dauth.user="${ADMIN_USERNAME}" \
      -Dauth.password="${ADMIN_PASSWORD}" \
      -Dwebapi.key="${WEBAPI_KEY}" \
      -Dwebapi.value="${WEBAPI_VALUE}" \
      -Djdk.tls.client.protocols="TLSv1,TLSv1.1,TLSv1.2"

CMD java -jar target/steve.jar
