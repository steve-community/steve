# Build stage
FROM eclipse-temurin:21-jdk AS builder
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

WORKDIR /code

# Copy relevant project files to the build stage
ADD /src /code/src
ADD /pom.xml /code/pom.xml
ADD mvnw /code/mvnw
ADD .mvn /code/.mvn

# Build the app
RUN ./mvnw clean package -Pdocker -Djdk.tls.client.protocols="TLSv1.2,TLSv1.3"

# Runtime stage
FROM eclipse-temurin:21-jre
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

# Create a non-root user and group
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

WORKDIR /code

# Copy only the built WAR from builder stage and set ownership
COPY --chown=appuser:appgroup --from=builder /code/target/steve.war ./steve.war

EXPOSE 8180
EXPOSE 8443

# Switch to the non-root user
USER appuser

# Run the app
CMD ["java", "-Djdk.tls.client.protocols=TLSv1.2,TLSv1.3", "-XX:MaxRAMPercentage=85", "-jar", "steve.war"]
