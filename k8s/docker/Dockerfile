FROM eclipse-temurin:21-jdk AS base
MAINTAINER daynnnnn

# Setting environment variables
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

# Arguments for database configuration
ARG DB_HOST
ARG DB_USERNAME
ARG DB_PASSWORD
ARG DB_DATABASE
ARG DB_PORT

# Build Stage
FROM base as build

# Set the working directory for the build stage
WORKDIR /code

# Copy project files to the build stage
ADD /src /code/src
ADD /website /code/website
ADD /pom.xml /code/pom.xml
ADD mvnw /code/mvnw
ADD .mvn /code/.mvn

# Replace placeholders in pom.xml with actual environment values
RUN sed -i 's|${db.ip}|${env.DB_HOST}|g' pom.xml
RUN sed -i 's|${db.port}|${env.DB_PORT}|g' pom.xml
RUN sed -i 's|${db.user}|${env.DB_USERNAME}|g' pom.xml
RUN sed -i 's|${db.password}|${env.DB_PASSWORD}|g' pom.xml
RUN sed -i 's|${db.schema}|${env.DB_DATABASE}|g' pom.xml
RUN sed -i 's|${server.host}|${env.SERVER_HOST}|g' pom.xml

# Make the Maven wrapper executable
RUN chmod +x mvnw

# Build the project
RUN ./mvnw clean package -Pkubernetes -Djdk.tls.client.protocols="TLSv1,TLSv1.1,TLSv1.2"

# Release Stage
FROM base AS release

# Copy relevant files from the build stage
COPY --from=build /code/target/steve.jar /app/steve.war
COPY --from=build /code/target/libs /app/libs

# Expose any necessary ports (example: 8080)
EXPOSE 8080

# Define the entrypoint for the release stage
CMD ["java", "-jar", "/app/steve.war"]
