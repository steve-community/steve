FROM eclipse-temurin:21-jdk
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8

EXPOSE 8180
EXPOSE 8443
WORKDIR /code

VOLUME ["/code"]

# Copy the application's code
COPY . /code

# Build and run steve, requires a db to be available on port 3306
CMD ./mvnw clean package -Pdocker -Djdk.tls.client.protocols="TLSv1,TLSv1.1,TLSv1.2" && \
    java -XX:MaxRAMPercentage=85 -jar target/steve.war
