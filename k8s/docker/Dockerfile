FROM eclipse-temurin:17-jdk
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

RUN ./mvnw clean package -Pkubernetes -Djdk.tls.client.protocols="TLSv1,TLSv1.1,TLSv1.2"

CMD java -jar target/steve.jar

