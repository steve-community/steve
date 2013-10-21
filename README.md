![SteVe](src/main/webapp/logo.png)
=====

SteVe was developed at the RWTH Aachen University and means Steckdosenverwaltung, namely socket administration in German. The aim of SteVe is to support the deployment and popularity of electric mobility, so it is easy to install and to use. SteVe provides basic functions for the administration of charge points, user data, and RFID cards for user authentication. It supports Open Charge Point Protocol (OCPP) 1.5 and 1.2 and was tested successfully in operation.

SteVe is considered as an open platform to implement, test and evaluate novel ideas for electric mobility, like authentication protocols, reservation mechanisms for charge points, and business models for electric mobility. SteVe is distributed under GPL and is free to use. If you are going to deploy SteVe we are happy to see the logo on a charge point.

How to use?
-----

1. SteVe is designed as a Web application to run under Apache Tomcat. Therefore, the source code must be compiled and deployed in Tomcat before use.  
**Important**: For security reasons, the Web interface can only be accessed with a username and password. By default, it grants access to users with Tomcat's *admin* role. This should be changed according to your preference in [web.xml](src/main/webapp/WEB-INF/web.xml)

2. SteVe requires MySQL to store the data. SteVe comes with a MySQL [dump file](steve-dump-no-data.sql) that contains the database structure for all tables with no data. This should be imported in your MySQL database before deployment of SteVe.  
**Important**: The default configuration for the database connection can be changed in [context.xml](src/main/webapp/META-INF/context.xml). The required fields to change are *username*, *password* and *url*.

Screenshots
-----
1. [Data Management - Reservations](https://raw.github.com/RWTH-i5-IDSG/steve/master/src/main/webapp/images/SteVe_res.png)
2. [Data Management - Charge Points](https://raw.github.com/RWTH-i5-IDSG/steve/master/src/main/webapp/images/SteVe_cp.png)
3. [Data Management - Users](https://raw.github.com/RWTH-i5-IDSG/steve/master/src/main/webapp/images/SteVe_users.png)
4. [Operations - OCPP v1.2](https://raw.github.com/RWTH-i5-IDSG/steve/master/src/main/webapp/images/SteVe_ocpp12.png)
5. [Operations - OCPP v1.5](https://raw.github.com/RWTH-i5-IDSG/steve/master/src/main/webapp/images/SteVe_ocpp15.png)
6. [Settings](https://raw.github.com/RWTH-i5-IDSG/steve/master/src/main/webapp/images/SteVe_set.png)
