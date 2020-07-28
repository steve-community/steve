FROM maven:3.6.1-jdk-11
MAINTAINER daynnnnn

ENV LANG=C.UTF-8 LC_ALL=C.UTF-8
ENV DB_PORT=3307

ARG DB_HOST
ARG DB_USERNAME
ARG DB_PASSWORD
ARG DB_DATABASE

WORKDIR /code

ADD /src /code/src
ADD /website /code/website
ADD /pom.xml /code/pom.xml

RUN sed -i 's|<jdbcUrl>jdbc:mysql://${db.ip}:${db.port}/${db.schema}?useSSL=true\&amp;serverTimezone=UTC</jdbcUrl>|<jdbcUrl>jdbc:mysql://${env.DB_HOST}:${env.DB_PORT}/${env.DB_DATABASE}?useSSL=true\&amp;serverTimezone=UTC</jdbcUrl>|g' pom.xml 
RUN sed -i 's|<user>${db.user}</user>|<user>${env.DB_USERNAME}</user>|g' pom.xml
RUN sed -i 's|<password>${db.password}</password>|<password>${env.DB_PASSWORD}</password>|g' pom.xml

RUN mvn clean package -Pkubernetes -Djdk.tls.client.protocols="TLSv1,TLSv1.1,TLSv1.2"

CMD java -jar target/steve.jar

