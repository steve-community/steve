# Build stage
FROM eclipse-temurin:21-jdk AS builder
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

# Arguments for database configuration
ARG DB_HOST
ARG DB_USERNAME
ARG DB_PASSWORD
ARG DB_DATABASE
ARG DB_PORT

WORKDIR /code

# Copy relevant project files to the build stage
ADD /src /code/src
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

# Build the app
RUN ./mvnw clean package -Pkubernetes -Djdk.tls.client.protocols="TLSv1.2,TLSv1.3"

# Runtime stage
FROM eclipse-temurin:21-jre
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

# Create a non-root user and group
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

WORKDIR /code

# Copy only the built WAR from builder stage and set ownership
COPY --chown=appuser:appgroup --from=builder /code/target/steve.war ./steve.war

# Expose any necessary ports (example: 8080)
EXPOSE 8080

# Switch to the non-root user
USER appuser

# Run the app
CMD ["java", "-Djdk.tls.client.protocols=TLSv1.2,TLSv1.3", "-jar", "steve.war"]
