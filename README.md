![SteVe](src/main/resources/webapp/static/images/logo.png) 
=====
[![Build Status](https://travis-ci.org/RWTH-i5-IDSG/steve.svg)](https://travis-ci.org/RWTH-i5-IDSG/steve)
[![Coverity Status](https://scan.coverity.com/projects/6601/badge.svg)](https://scan.coverity.com/projects/rwth-i5-idsg-steve)

SteVe was developed at the RWTH Aachen University and means Steckdosenverwaltung, namely socket administration in German. The aim of SteVe is to support the deployment and popularity of electric mobility, so it is easy to install and to use. SteVe provides basic functions for the administration of charge points, user data, and RFID cards for user authentication and was tested successfully in operation.

SteVe is considered as an open platform to implement, test and evaluate novel ideas for electric mobility, like authentication protocols, reservation mechanisms for charge points, and business models for electric mobility. SteVe is distributed under [GPL](LICENSE.txt) and is free to use. If you are going to deploy SteVe we are happy to see the [logo](website/logo/managed-by-steve.pdf) on a charge point.

Supported OCPP protocols
-----

* OCPP1.2S
* OCPP1.5S
* OCPP1.2J
* OCPP1.5J

Requirements & Configuration
-----

1. You need JDK 8, Maven and MySQL.

2. The charge points must be configured to communicate with following addresses. Depending on the OCPP version of the charge point, SteVe will automatically route messages to the version-specific implementation.
  - SOAP: `http://<your-server-ip>:<port>/steve/services/CentralSystemService`
  - WebSocket/JSON: `ws://<your-server-ip>:<port>/steve/websocket/CentralSystemService/<chargeBoxId>`
   
3. The following MySQL statements can be used as database initialization (adjust according to your setup):

    ```
    CREATE DATABASE stevedb;
    GRANT ALL PRIVILEGES ON stevedb.* TO 'steve'@'localhost' IDENTIFIED BY 'changeme';
    GRANT SELECT ON mysql.proc TO 'steve'@'localhost' IDENTIFIED BY 'changeme';
    ```

4. Basic Configuration is defined in [main.properties](src/main/resources/config/prod/main.properties):
  - You _must_ change [database configuration](src/main/resources/config/prod/main.properties#L3-L7)
  - You _must_ change [the host](src/main/resources/config/prod/main.properties#L16) to the correct IP address of your server. Using `0.0.0.0` to bind to all Interfaces should work as well, but might have security implications
  - You _must_ change [web interface credentials](src/main/resources/config/prod/main.properties#L11-L12)
  - You _can_ access the application via HTTPS, by [enabling it and setting the keystore properties](src/main/resources/config/prod/main.properties#L25-L28)
 
5. Log File Configuration is defined in [log4j2.xml](src/main/resources/config/prod/log4j2.xml):
  - You _can_ change the [log directory](src/main/resources/config/prod/log4j2.xml#L10). 
  The default location is `$HOME/logs/steve.log`. A more suitable location might be `/var/log/steve/steve.log` (remember to set directory permissions accordingly)
  - You _can_ change the [log level](src/main/resources/config/prod/log4j2.xml#L32)

How to use?
-----

Compile and build an archive ready to run:

    mvn package
 
Start the application (please do not run SteVe as root):

    java -jar target/steve-*.jar

Access the Web interface:

    http://<your-server-ip>:<port>/steve/manager

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
