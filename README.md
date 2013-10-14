![SteVe](src/main/webapp/logo.png)
=====

SteVe was developed at the RWTH Aachen University and means Steckdosenverwaltung, namely power socket administration in German. The aim of SteVe is to support the deployment and popularity of electric mobility, so it is easy to install and to use. SteVe provides basic functions for the administration of charging points, user data, and RFID cards for user authentication. It supports Open Charge Point Protocol (OCPP) 1.5 and 1.2 and was tested successfully in operation.

SteVe is considered as an open platform to implement, test and evaluate novel ideas for electric mobility, like authentication protocols, reservation mechanisms for charge points, and business models for electric mobility. SteVe is distributed under GPL and is free to use. If you are going to deploy SteVe we are happy to see the logo on a charge point.

How to use?
-----

1. SteVe is designed as a Web application to run under Apache Tomcat. Therefore, the source code must be compiled and deployed in Tomcat before use.  
Important: For security reasons, SteVe's Web interface can only be accessed with Tomcat's *admin* role by default. This should be changed according to your preference in [web.xml](src/main/webapp/WEB-INF/web.xml)

2. SteVe requires MySQL to store the data. SteVe comes with a MySQL [dump file](steve-dump-no-data.sql) that contains the database structure for all tables with no data. This should be imported in your MySQL database before deployment of SteVe.  
Important: The default configuration for the database connection can be changed in [context.xml](src/main/webapp/META-INF/context.xml). The required fields to change are *username*, *password* and *url*.


