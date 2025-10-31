FROM eclipse-temurin:21-jdk

ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

# Install dockerize, and also add 'unzip' which we will need
ENV DOCKERIZE_VERSION=v0.19.0
RUN apt-get update && apt-get install -y unzip curl ca-certificates ca-certificates-java && \
    update-ca-certificates -f && \
    curl -sfL https://github.com/powerman/dockerize/releases/download/"$DOCKERIZE_VERSION"/dockerize-`uname -s`-`uname -m` | install /dev/stdin /usr/local/bin/dockerize

EXPOSE 8180
EXPOSE 8443
WORKDIR /code

VOLUME ["/code"]

# Copy the application's code
COPY . /code

# --- THIS IS THE FIX ---
# 1. Read the URL from the wrapper's config file
RUN MVN_URL="$(grep 'distributionUrl' .mvn/wrapper/maven-wrapper.properties | cut -d'=' -f2 | sed 's#\\/#/#g')" && \
    echo "Downloading Maven from: $MVN_URL" && \
    curl -k -L -o maven.zip "$MVN_URL"
RUN unzip maven.zip
ENV MVN_DIR $(find . -type d -name "apache-maven-*" | head -n 1)

# Ensure Java truststore is synced with system CA certificates
# The ca-certificates-java package should create /etc/ssl/certs/java/cacerts
# RUN if [ -x /usr/sbin/update-ca-certificates-java ]; then \
#     /usr/sbin/update-ca-certificates-java 2>/dev/null || true; \
#     fi && \
#     if [ -f /etc/ssl/certs/java/cacerts ]; then \
#     cp /etc/ssl/certs/java/cacerts "$JAVA_HOME/lib/security/cacerts"; \
#     fi

# # If a corporate CA cert is mounted at /etc/ssl/certs/corporate-ca.pem, import it
# RUN if [ -f /etc/ssl/certs/corporate-ca.pem ]; then \
#     keytool -importcert -noprompt -trustcacerts \
#       -alias corporate-ca \
#       -file /etc/ssl/certs/corporate-ca.pem \
#       -keystore "$JAVA_HOME/lib/security/cacerts" \
#       -storepass changeit; \
#     fi

# Wait for the db, then build and run steve.
# This CMD now calls the 'mvn' binary directly, completely bypassing the wrapper.
# Your flags will now be correctly passed to Maven.
# CMD dockerize -wait tcp://mariadb:3306 -timeout 60s && \
# export MAVEN_OPTS="-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true" \
# && ./$(find . -type d -name "apache-maven-*" | head -n 1)/bin/mvn -U -e clean package -Pkubernetes -Djdk.tls.client.protocols="TLSv1,TLSv1.1,TLSv1.2" \
# && java -XX:MaxRAMPercentage=85 -jar target/steve.war
CMD ["tail", "-f", "/dev/null"]