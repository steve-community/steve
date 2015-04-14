![SteVe](src/main/resources/webapp/static/images/logo.png)
=====

SteVe was developed at the RWTH Aachen University and means Steckdosenverwaltung, namely socket administration in German. The aim of SteVe is to support the deployment and popularity of electric mobility, so it is easy to install and to use. SteVe provides basic functions for the administration of charge points, user data, and RFID cards for user authentication and was tested successfully in operation.

SteVe is considered as an open platform to implement, test and evaluate novel ideas for electric mobility, like authentication protocols, reservation mechanisms for charge points, and business models for electric mobility. SteVe is distributed under GPL and is free to use. If you are going to deploy SteVe we are happy to see the logo on a charge point.

Supported OCPP protocols
-----

* OCPP1.2S
* OCPP1.5S
* OCPP1.2J
* OCPP1.5J

Requirements & Configuration
-----

1. You need JDK 7, Maven and MySQL.

2. The charge points must be configured to communicate with following addresses. Depending on the OCPP version of the charge point, SteVe will automatically route messages to the version-specific implementation.
  - SOAP: `http://<your-server-ip>:<port>/steve/services/CentralSystemService`
  - WebSocket/JSON: `ws://<your-server-ip>:<port>/steve/websocket/CentralSystemService/<chargeBoxId>`
   
3. Configuration is defined in [main.properties](src/main/resources/main.properties):
  - You _must_ change [database configuration](src/main/resources/main.properties#L3-L5)
  - You _can_ change [web interface credentials](src/main/resources/main.properties#L9-L10)
  - You _can_ access the application via HTTPS, by [enabling it and setting the keystore properties](src/main/resources/main.properties#L23-L26)
  
How to use?
-----

Compile and build an archive ready to run:

    mvn package
 
Start the application:

    java -jar steve-***.jar

Access the Web interface:

    http://localhost:8080/steve/manager

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

Change log
-----
See the [CHANGELOG](CHANGELOG.md)
