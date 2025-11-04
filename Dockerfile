FROM eclipse-temurin:21-jdk

ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

# Install dockerize, and also add 'unzip' which we will need
ENV DOCKERIZE_VERSION=v0.19.0
RUN apt-get update && apt-get install -y unzip curl net-tools dnsutils ca-certificates ca-certificates-java && \
    update-ca-certificates -f && \
    curl -sfL https://github.com/powerman/dockerize/releases/download/"$DOCKERIZE_VERSION"/dockerize-`uname -s`-`uname -m` | install /dev/stdin /usr/local/bin/dockerize

EXPOSE 8180
EXPOSE 8443
WORKDIR /code

VOLUME ["/code"]

# Copy the application's code
COPY . /code

# Copy and set up entrypoint script for certificate import
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# Set entrypoint to handle certificate imports at runtime
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]

# Wait for the db, then build and run steve.
# This CMD now calls the 'mvn' binary directly, completely bypassing the wrapper.
# Your flags will now be correctly passed to Maven.
# CMD dockerize -wait tcp://mariadb:3306 -timeout 60s && \
# export MAVEN_OPTS="-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true" \
# && ./$(find . -type d -name "apache-maven-*" | head -n 1)/bin/mvn -U -e clean package -Pkubernetes -Djdk.tls.client.protocols="TLSv1,TLSv1.1,TLSv1.2" \
# && java -XX:MaxRAMPercentage=85 -jar target/steve.war
CMD ["tail", "-f", "/dev/null"]