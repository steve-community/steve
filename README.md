![SteVe](src/main/resources/webapp/static/images/logo.png) 

[![Build Status](https://travis-ci.org/RWTH-i5-IDSG/steve.svg)](https://travis-ci.org/RWTH-i5-IDSG/steve)
[![Coverity Status](https://scan.coverity.com/projects/6601/badge.svg)](https://scan.coverity.com/projects/rwth-i5-idsg-steve)


# Introduction

SteVe was developed at the RWTH Aachen University and means Steckdosenverwaltung, namely socket administration in German. The aim of SteVe is to support the deployment and popularity of electric mobility, so it is easy to install and to use. SteVe provides basic functions for the administration of charge points, user data, and RFID cards for user authentication and was tested successfully in operation.

SteVe is considered as an open platform to implement, test and evaluate novel ideas for electric mobility, like authentication protocols, reservation mechanisms for charge points, and business models for electric mobility. SteVe is distributed under [GPL](LICENSE.txt) and is free to use. If you are going to deploy SteVe we are happy to see the [logo](website/logo/managed-by-steve.pdf) on a charge point.

### Charge Point Support

Electric charge points using the following OCPP versions are supported:

* OCPP1.2S
* OCPP1.5S
* OCPP1.2J
* OCPP1.5J

We have successfully tested SteVe with charge points manufactured by EBG and Mennekes. If your charge point also works well with SteVe, please let us know! We will update the list.

### System Requirements

SteVe requires 
* JDK 8 (both Oracle JDK and OpenJDK are supported)
* Maven 
* At least MySQL 5.6.4 (MariaDB 10.0 or later works as well) as database

to build and run. 

SteVe is designed to run standalone, a java servlet container / web server (e.g. Apache Tomcat), is **not** required.

# Configuration and Installation

1. Database preparation:

    The following MySQL statements can be used as database initialization (adjust according to your setup):

    ```
    CREATE DATABASE stevedb CHARACTER SET utf8 COLLATE utf8_unicode_ci;
    GRANT ALL PRIVILEGES ON stevedb.* TO 'steve'@'localhost' IDENTIFIED BY 'changeme';
    GRANT SELECT ON mysql.proc TO 'steve'@'localhost' IDENTIFIED BY 'changeme';
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
      - You _must_ change [database configuration](src/main/resources/config/prod/main.properties#L3-L7)
      - You _must_ change [the host](src/main/resources/config/prod/main.properties#L16) to the correct IP address of your server. Using `0.0.0.0` to bind to all Interfaces should work as well, but might have security implications
      - You _must_ change [web interface credentials](src/main/resources/config/prod/main.properties#L11-L12)
      - You _can_ access the application via HTTPS, by [enabling it and setting the keystore properties](src/main/resources/config/prod/main.properties#L25-L28)
     
    For advanced configuration please see the [Configuration wiki](https://github.com/RWTH-i5-IDSG/steve/wiki/Configuration)

4. Build SteVe:

    To compile SteVe simply use Maven. A runnable `jar` file containing the application and configuration will be created in the subdirectory `steve/target`.

    ```
    # mvn package
    ```

5. Run SteVe:

    To start the application run (please do not run SteVe as root):

    ```
    # java -jar target/steve-X.X.X.jar
    ```

# First Steps

After SteVe has successfully started, you can access the web interface using the configured credentials under:

    http://<your-server-ip>:<port>/steve/manager
    
The default port number is 8080.

### Add a charge point

1. In order for SteVe to accept messages from a charge point, the charge point must first be registered. To add a charge point to SteVe select *Data Management* >> *Charge Points* >> *Add*. Enter the ChargeBox ID configured in the charge point and confirm.

2. The charge points must be configured to communicate with following addresses. Depending on the OCPP version of the charge point, SteVe will automatically route messages to the version-specific implementation.
    - SOAP: `http://<your-server-ip>:<port>/steve/services/CentralSystemService`
    - WebSocket/JSON: `ws://<your-server-ip>:<port>/steve/websocket/CentralSystemService/<chargeBoxId>`


As soon as a heartbeat is received, you should see the status of the charge point in the SteVe Dashboard.
 
*Have fun!*

Screenshots
-----
1. [Home](website/screenshots/home.png)
2. [Heartbeats](website/screenshots/heartbeats.png)
3. [Connector Status](website/screenshots/connector-status.png)
4. [Data Management - Reservations](website/screenshots/reservations.png)
5. [Data Management - Charge Points](website/screenshots/chargepoints.png)
6. [Data Management - Users](website/screenshots/users.png)
7. [Data Management - Transactions](website/screenshots/transactions.png)
8. [Operations - OCPP v1.2](website/screenshots/ocpp12.png)
9. [Operations - OCPP v1.5](website/screenshots/ocpp15.png)
10. [Settings](website/screenshots/settings.png)

Are you having issues?
-----
See the [FAQ](https://github.com/RWTH-i5-IDSG/steve/wiki/FAQ)

