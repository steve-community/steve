## Disclaimer: This branch is for migrating to latest versions of dependencies plus Jetty, Jooq, Spring MVC and Hibernate Validator. The aim is to refactor in order to improve the overall design. Everything is experimental, unfinished, subject to change and break. Don't use it. When done, it will be merged with the master branch. So... Really, don't use it.

---

![SteVe](src/main/resources/webapp/static/images/logo.png)
=====

SteVe was developed at the RWTH Aachen University and means Steckdosenverwaltung, namely socket administration in German. The aim of SteVe is to support the deployment and popularity of electric mobility, so it is easy to install and to use. SteVe provides basic functions for the administration of charge points, user data, and RFID cards for user authentication. It supports Open Charge Point Protocol (OCPP) 1.5 and 1.2 and was tested successfully in operation.

SteVe is considered as an open platform to implement, test and evaluate novel ideas for electric mobility, like authentication protocols, reservation mechanisms for charge points, and business models for electric mobility. SteVe is distributed under GPL and is free to use. If you are going to deploy SteVe we are happy to see the logo on a charge point.

Requirements & Configuration
-----

1. You need Maven, MySQL and JDK 7. 

2. Before using SteVe, the default database configuration (*username*, *password* and *url*) has to be changed in [pom.xml](pom.xml#L18-20) and [SteveConfiguration.java](src/main/java/de/rwth/idsg/steve/SteveConfiguration.java#L53-L55).

3. Web interface credentials are defined in [SteveConfiguration.java](src/main/java/de/rwth/idsg/steve/SteveConfiguration.java#L65-L66). Change them if you wish.

4. The HTTP URL of the OCPP service is **http://**`your-server-ip:port`**/steve/services/CentralSystemService**. The charge points must be configured to communicate with this address. Depending on the OCPP version of the charge point, SteVe will automatically route messages to the version-specific implementation.

5. Optional: To access the application via HTTPS, ...

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
1. [Home](https://raw.github.com/RWTH-i5-IDSG/steve/master/resources/screenshots/home.png)
2. [Heartbeats](https://raw.github.com/RWTH-i5-IDSG/steve/master/resources/screenshots/heartbeats.png)
3. [Connector Status](https://raw.github.com/RWTH-i5-IDSG/steve/master/resources/screenshots/connector-status.png)
4. [Data Management - Reservations](https://raw.github.com/RWTH-i5-IDSG/steve/master/resources/screenshots/reservations.png)
5. [Data Management - Charge Points](https://raw.github.com/RWTH-i5-IDSG/steve/master/resources/screenshots/chargepoints.png)
6. [Data Management - Users](https://raw.github.com/RWTH-i5-IDSG/steve/master/resources/screenshots/users.png)
7. [Data Management - Transactions](https://raw.github.com/RWTH-i5-IDSG/steve/master/resources/screenshots/transactions.png)
8. [Operations - OCPP v1.2](https://raw.github.com/RWTH-i5-IDSG/steve/master/resources/screenshots/ocpp12.png)
9. [Operations - OCPP v1.5](https://raw.github.com/RWTH-i5-IDSG/steve/master/resources/screenshots/ocpp15.png)
10. [Settings](https://raw.github.com/RWTH-i5-IDSG/steve/master/resources/screenshots/settings.png)

Change log
-----
See the [CHANGELOG](CHANGELOG.md)

To-Do List
-----
1. Hubject/OICP integration
2. Pagination for data tables