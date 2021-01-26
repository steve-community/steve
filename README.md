![SteVe](src/main/resources/webapp/static/images/logo.png) 

[![Build Status](https://travis-ci.org/RWTH-i5-IDSG/steve.svg?branch=master)](https://travis-ci.org/RWTH-i5-IDSG/steve)


# Introduction

SteVe was developed at the RWTH Aachen University and means Steckdosenverwaltung, namely socket administration in German. The aim of SteVe is to support the deployment and popularity of electric mobility, so it is easy to install and to use. SteVe provides basic functions for the administration of charge points, user data, and RFID cards for user authentication and was tested successfully in operation.

SteVe is considered as an open platform to implement, test and evaluate novel ideas for electric mobility, like authentication protocols, reservation mechanisms for charge points, and business models for electric mobility. SteVe is distributed under [GPL](LICENSE.txt) and is free to use.

### Modifications by Parkl

SteVe originally was developed as a deployable web application. We wanted to provide a simple, installable, platform-independent library that provides interfaces for the SteVe functionality and meets the following requirements:

* the library can be installed using Maven, as a single JAR file
* the library should use Spring Boot
* the library should be decoupled from any database server (such as MySQL) using an abstraction for the data access layer (Spring Data JPA)
* the library should publish interfaces for an e-mobility service provider to send and receive events to/from a backend

In order to achieve the above goals, the following technical changes have been made:
* migration from Spring to Spring Boot
* migration from JOGL to Spring Data JPA
* removing MySQL dependent functionality
* migration from Jetty websockets to Apache Tomcat websockets
* introduction of the EmobilityServiceProvider and EmobilityServiceProviderFacade interfaces to communicate with an ESP backend


### Charge Point Support

Electric charge points using the following OCPP versions are supported:

* OCPP1.2S
* OCPP1.2J
* OCPP1.5S
* OCPP1.5J
* OCPP1.6S
* OCPP1.6J

For Charging Station compatibility please check:
https://github.com/RWTH-i5-IDSG/steve/wiki/Charging-Station-Compatibility

### System Requirements

SteVe requires 
* JDK 11 (both Oracle JDK and OpenJDK are supported)
* Maven 
* At least MySQL 5.7.7 (MariaDB 10.2.1 or later works as well) as database

to build and run. 

SteVe is designed to run standalone, a java servlet container / web server (e.g. Apache Tomcat), is **not** required.

# Configuration and Installation

1. Database preparation:

    **Important**: Make sure that the time zone of the MySQL server is the same as [the time zone of SteVe](src/main/java/de/rwth/idsg/steve/SteveConfiguration.java#L46). Since `UTC` is strongly recommended by OCPP, it is the default in SteVe and you should set it in MySQL, accordingly.

    Make sure MySQL is reachable via TCP (e.g., remove `skip-networking` from `my.cnf`).
    The following MySQL statements can be used as database initialization (adjust database name and credentials according to your setup).
    
    * For MySQL 5.7:
        ```
        CREATE DATABASE stevedb CHARACTER SET utf8 COLLATE utf8_unicode_ci;
        GRANT ALL PRIVILEGES ON stevedb.* TO 'steve'@'localhost' IDENTIFIED BY 'changeme';
        GRANT SELECT ON mysql.proc TO 'steve'@'localhost' IDENTIFIED BY 'changeme';
        ```
    
    * For MySQL 8:
        ```
        CREATE DATABASE stevedb CHARACTER SET utf8 COLLATE utf8_unicode_ci;
        CREATE USER 'steve'@'localhost' IDENTIFIED BY 'changeme';
        GRANT ALL PRIVILEGES ON stevedb.* TO 'steve'@'localhost';
        GRANT SUPER ON *.* TO 'steve'@'localhost';
        ```
        Note: The statement `GRANT SUPER [...]` is only necessary to execute some of the previous migration files and is only needed for the initial database setup. Afterwards, you can remove this privilage by executing 
        ```
        REVOKE SUPER ON *.* FROM 'steve'@'localhost';
        ```
        
2. Download and extract tarball:

    You can download and extract the SteVe releases using the following commands (replace X.X.X with the desired version number):
    ```
    wget https://github.com/RWTH-i5-IDSG/steve/archive/steve-X.X.X.tar.gz
    tar xzvf steve-X.X.X.tar.gz
    cd steve-X.X.X
    ```

3. Configure SteVe **before** building:

    The basic configuration is defined in [main.properties](src/main/resources/config/prod/main.properties):
      - You _must_ change [database configuration](src/main/resources/config/prod/main.properties#L9-L13)
      - You _must_ change [the host](src/main/resources/config/prod/main.properties#L22) to the correct IP address of your server
      - You _must_ change [web interface credentials](src/main/resources/config/prod/main.properties#L17-L18)
      - You _can_ access the application via HTTPS, by [enabling it and setting the keystore properties](src/main/resources/config/prod/main.properties#L32-L35)
     
    For advanced configuration please see the [Configuration wiki](https://github.com/RWTH-i5-IDSG/steve/wiki/Configuration)

4. Build SteVe:

    To compile SteVe simply use Maven. A runnable `jar` file containing the application and configuration will be created in the subdirectory `steve/target`.

    ```
    # mvn package
    ```

5. Run SteVe:

    To start the application run (please do not run SteVe as root):

    ```
    # java -jar target/steve.jar
    ```

# Docker

If you prefer to build and start this project via docker (you can skip the steps 1 and 3, 4, 5 above), this can be done as follows: `docker-compose up -d `

The web interface will be accessible at: `http://localhost:8180`

# Kubernetes

First build your image, and push it to a registry your K8S cluster can access. Make sure the build args in the docker build command are set with the same database configuration that the main deployment will use.

`docker build --build-arg DB_HOST= --build-arg DB_PORT= --build-arg DB_USERNAME= --build-arg DB_PASSWORD= --build-arg DB_DATABASE=  -f k8s/docker/Dockerfile -t <IMAGE_NAME> .`

`docker push <IMAGE_NAME>`


Then go to `k8s/yaml/Deployment.yaml` and change `### YOUR BUILT IMAGE HERE ###` to your image tag, and fill in the environment variables with the same database connection that you used at build time.

After this, create the namespace using `kubectl create ns steve` and apply your yaml with `kubectl apply -f k8s/yaml/Deployment.yaml` followed by `kubectl apply -f k8s/yaml/Service.yaml`


To access this publicaly, you'll also have to setup an ingress using something like nginx or traefik. 

# Ubuntu

You'll find a tutorial how to prepare Ubuntu for SteVe here: https://github.com/RWTH-i5-IDSG/steve/wiki/Prepare-Ubuntu-VM-for-SteVe

# AWS

You'll find a tutorial how to setup SteVe in AWS using Lightsail here: https://github.com/RWTH-i5-IDSG/steve/wiki/Create-SteVe-Instance-in-AWS-Lightsail

# First Steps

After SteVe has successfully started, you can access the web interface using the configured credentials under:

    http://<your-server-ip>:<port>/steve/manager
    
The default port number is 8080.

### Add a charge point

1. In order for SteVe to accept messages from a charge point, the charge point must first be registered. To add a charge point to SteVe select *Data Management* >> *Charge Points* >> *Add*. Enter the ChargeBox ID configured in the charge point and confirm.

2. The charge points must be configured to communicate with following addresses. Depending on the OCPP version of the charge point, SteVe will automatically route messages to the version-specific implementation.
    - SOAP: `http://<your-server-ip>:<port>/steve/services/CentralSystemService`
    - WebSocket/JSON: `ws://<your-server-ip>:<port>/steve/websocket/CentralSystemService`


As soon as a heartbeat is received, you should see the status of the charge point in the SteVe Dashboard.
 
*Have fun!*

Screenshots
-----
1. [Home](website/screenshots/home.png)
2. [Connector Status](website/screenshots/connector-status.png)
3. [Data Management - Charge Points](website/screenshots/chargepoints.png)
4. [Data Management - Users](website/screenshots/users.png)
5. [Data Management - OCPP Tags](website/screenshots/ocpp-tags.png)
6. [Data Management - Reservations](website/screenshots/reservations.png)
7. [Data Management - Transactions](website/screenshots/transactions.png)
8. [Operations - OCPP v1.2](website/screenshots/ocpp12.png)
9. [Operations - OCPP v1.5](website/screenshots/ocpp15.png)
10. [Settings](website/screenshots/settings.png)

GDPR
-----
If you are in the EU and offer vehicle charging to other people using SteVe, keep in mind that you have to comply to the General Data Protection Regulation (GDPR) as SteVe processes charging transactions, which can be considered personal data.

Are you having issues?
-----
See the [FAQ](https://github.com/RWTH-i5-IDSG/steve/wiki/FAQ)

