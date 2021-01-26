![SteVe](src/main/resources/webapp/static/images/logo.png) 
![Parkl](src/main/resources/webapp/static/images/parkl_logo.png) 


# Introduction

SteVe was developed at the RWTH Aachen University and means Steckdosenverwaltung, namely socket administration in German. The aim of SteVe is to support the deployment and popularity of electric mobility, so it is easy to install and to use. SteVe provides basic functions for the administration of charge points, user data, and RFID cards for user authentication and was tested successfully in operation.

SteVe is considered as an open platform to implement, test and evaluate novel ideas for electric mobility, like authentication protocols, reservation mechanisms for charge points, and business models for electric mobility. SteVe is distributed under [GPL](LICENSE.txt) and is free to use.

### Modifications by Parkl: SteVe Pluggable library

SteVe originally was developed as a deployable web application. We wanted to provide a simple, installable, platform-independent library that provides interfaces for the SteVe functionality and meets the following requirements:

* the library can be installed using Maven, as a single JAR file
* the library should use Spring Boot
* the library should be decoupled from any database server (such as MySQL) using an abstraction for the data access layer (Spring Data JPA)
* the library should publish interfaces for an e-mobility service provider to send and receive events to/from an e-mobility service provider (ESP) backend

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


### System Requirements

SteVe requires 
* JDK 11 (both Oracle JDK and OpenJDK are supported)
* Maven 
* Any JPA compatible database server

to build and run. 

SteVe Pluggable is intended to run in any servlet container or in embedded web application, however currently only Apache Tomcat is supported. This mainly because of the websocket server and JSP compiler provided by Tomcat. We plan to change the JSP frontend to React and use a Tomcat-independent websocket server implementation in the near future, making the library completely container independent.

# Configuration and Installation

1. Install library

    Edit your web application project's pom.xml file to include **Steve Pluggable**:
    ```
    <dependency>
            <groupId>net.parkl.ocpp</groupId>
            <artifactId>steve-pluggable</artifactId>
            <version>1.0.2</version>
    </dependency>
    ```
    
        
2. Configure Spring Boot:

    An application that uses **SteVe Pluggable** should be configured in the standard Spring boot manner.
    An example datasource configuration that uses environment variables:
    ```
    spring.datasource.username=${db.ocpp.user}
    spring.datasource.password=${db.ocpp.password}
    spring.datasource.url=jdbc:mysql://${db.ocpp.host}/${db.ocpp.name}?useUnicode=true&characterEncoding=UTF-8&serverTimezone=CET
    spring.datasource.driver-class-name=com.mysql.jdbc.Driver
    spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
    spring.jpa.open-in-view=false
    spring.jpa.generate-ddl=true
    spring.jpa.hibernate.ddl-auto=update
    ```
    
    OCPP specific configuration
    ```
    # When the WebSocket/Json charge point opens more than one WebSocket connection,
    # we need a mechanism/strategy to select one of them for outgoing requests.
    # For allowed values see de.rwth.idsg.steve.ocpp.ws.custom.WsSessionSelectStrategyEnum.
    #
    ocpp.ws.session.select.strategy=ALWAYS_LAST
    ```
    
    OCPP server admin frontend authentication configuration (password supplied in BCrypt hash):
    ```
    ocpp.auth.user=admin
    ocpp.auth.password=$BCRYPT_HASH$
    ```
    


