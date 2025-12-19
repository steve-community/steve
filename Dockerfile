FROM eclipse-temurin:21-jdk
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

WORKDIR /code

# Copy relevant project files to the build stage
ADD /src /code/src
ADD /pom.xml /code/pom.xml
ADD mvnw /code/mvnw
ADD .mvn /code/.mvn

# Build the app (requires a DB to be available)
RUN ./mvnw clean package -Pdocker

EXPOSE 8180
EXPOSE 8443

# Run the app (requires a DB to be available)
CMD ["java", "-XX:MaxRAMPercentage=85", "-Djdk.tls.client.protocols=TLSv1.2,TLSv1.3", "-jar", "target/steve.war"]
