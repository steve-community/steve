# --- STAGE 1: The Builder ---
# Use a full Maven + JDK image to build the app
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /code
COPY . /code

# Run the build, telling both the wrapper (via http) and 
# Maven (via the -D flag) to be insecure
RUN sed -i 's/https/http/' .mvn/wrapper/maven-wrapper.properties && \
    ./mvnw clean package -Pkubernetes -Dmaven.wagon.http.ssl.insecure=true


# --- STAGE 2: The Final Image ---
# Use a lightweight JRE-only image for runtime
FROM eclipse-temurin:21-jre
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

# Install dockerize
ENV DOCKERIZE_VERSION v0.19.0
RUN curl -sfL https://github.com/powerman/dockerize/releases/download/"$DOCKERIZE_VERSION"/dockerize-`uname -s`-`uname -m` | install /dev/stdin /usr/local/bin/dockerize

EXPOSE 8180
EXPOSE 8443
WORKDIR /app

# Copy ONLY the final .war file from the 'builder' stage
COPY --from=builder /code/target/steve.war /app/steve.war

# This command is now clean, fast, and only runs the app
CMD dockerize -wait tcp://mariadb:3306 -timeout 60s && \
    java -XX:MaxRAMPercentage=85 -jar /app/steve.war