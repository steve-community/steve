FROM eclipse-temurin:17-jdk

ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

MAINTAINER Ling Li

# Download and install dockerize.
# Needed so the web container will wait for MariaDB to start.
ENV DOCKERIZE_VERSION v0.19.0
RUN curl -sfL https://github.com/powerman/dockerize/releases/download/"$DOCKERIZE_VERSION"/dockerize-`uname -s`-`uname -m` | install /dev/stdin /usr/local/bin/dockerize

EXPOSE 8180
EXPOSE 8443
WORKDIR /code

VOLUME ["/code"]

# Copy the application's code
COPY . /code

# Wait for the db to startup(via dockerize), then 
# Build and run steve, requires a db to be available on port 3306
CMD dockerize -wait tcp://mariadb:3306 -timeout 60s && \
	./mvnw clean package -Pdocker -Djdk.tls.client.protocols="TLSv1,TLSv1.1,TLSv1.2" && \
	java -jar target/steve.jar

