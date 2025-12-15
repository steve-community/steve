# Build stage
FROM eclipse-temurin:21-jdk AS builder
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

WORKDIR /code

# Copy Maven wrapper, pom.xml and source code for better caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src/ ./src/

# Build the app
RUN ./mvnw clean package -Pdocker -Djdk.tls.client.protocols="TLSv1.2,TLSv1.3"

# Runtime stage
FROM eclipse-temurin:21-jre
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

WORKDIR /code

# Copy only the built WAR from builder stage
COPY --from=builder /code/target/steve.war ./steve.war

EXPOSE 8180
EXPOSE 8443

# Run the app
CMD ["java", "-XX:MaxRAMPercentage=85", "-jar", "steve.war"]
