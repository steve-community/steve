# Build stage
FROM eclipse-temurin:21-jdk AS builder
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

# Install MariaDB for build-time Flyway migrations and jOOQ code generation
RUN apt-get update && \
    apt-get install -y mariadb-server && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /code

# Copy relevant project files to the build stage
ADD /src /code/src
ADD /pom.xml /code/pom.xml
ADD mvnw /code/mvnw
ADD .mvn /code/.mvn

# Ensure mvnw is executable (Windows may strip the execute bit)
RUN chmod +x ./mvnw

# Start MariaDB temporarily, create the database/user, build the app, then stop MariaDB
RUN service mariadb start && \
    mysql -u root -e "CREATE DATABASE stevedb CHARACTER SET utf8 COLLATE utf8_unicode_ci;" && \
    mysql -u root -e "CREATE USER 'steve'@'localhost' IDENTIFIED BY 'changeme';" && \
    mysql -u root -e "GRANT ALL PRIVILEGES ON stevedb.* TO 'steve'@'localhost';" && \
    mysql -u root -e "FLUSH PRIVILEGES;" && \
    ./mvnw clean package -Pdocker \
        -Ddb.ip=localhost \
        -Ddb.port=3306 \
        -Ddb.schema=stevedb \
        -Ddb.user=steve \
        -Ddb.password=changeme \
        -Djdk.tls.client.protocols="TLSv1.2,TLSv1.3" && \
    service mariadb stop

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
