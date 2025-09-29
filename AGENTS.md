# Evolve Project: Agent Instructions

This document provides instructions for AI agents working on the Evolve project.

## Project Overview

Evolve is an open-source platform for managing electric vehicle charging stations. It is a fork of the SteVe (Steckdosenverwaltung) project. It supports various versions of the Open Charge Point Protocol (OCPP) and provides a web-based interface for administration.

The project is built with Java and Maven. It uses a MySQL or MariaDB database for data storage.

## Architecture and Frameworks

Evolve is designed using a hexagonal architecture (also known as Ports and Adapters or Clean Architecture). This separates the core application logic from the services it consumes and the interfaces it provides.

### Core Module

-   `steve-core`: This is the heart of the application. It contains the core business logic and is independent of any specific technology or framework for its external communication. It defines the "ports" (interfaces) for interacting with the outside world.

### Adapter Modules

The other modules act as "adapters" that implement the ports defined in `steve-core` or drive the application.

-   **Database Adapter:** `steve-jooq` implements the database persistence logic using the jOOQ framework. It connects the core application to a MySQL/MariaDB database.
-   **OCPP Adapters:** The `steve-ocpp-*` modules handle communication with charging stations using different versions and transports of the OCPP protocol.
-   **UI Adapter:** `steve-ui-jsp` provides a web-based user interface using JavaServer Pages (JSP).

### Key Frameworks

-   **Spring Framework:** Provides dependency injection, component management, and transaction control.
-   **Jetty:** Serves as the embedded web server, allowing the application to run as a standalone JAR.
-   **jOOQ:** Builds type-safe SQL queries for database interaction.
-   **Flyway:** Manages database schema migrations, executed during the build and at startup.
-   **Lombok:** Reduces boilerplate code for model and data objects.

## Getting Started

The easiest way to get the project up and running is by using Docker Compose. This will set up the application and the required database.

To start the project from the repository root, run:
```bash
docker compose up -d
```
The web interface will be available at `http://localhost:8180`.

## Building the Project

To build the project from source, you will need:
-   JDK 21 or newer
-   Maven 3.9.0 or newer
-   A running database instance (MySQL 8.0 or MariaDB 10.3+).

**IMPORTANT:** The build process requires a running database. It connects to the database to perform two key steps during the `generate-sources` phase, defined in the `steve-jooq/pom.xml`:

1.  **Database Migration (`flyway-maven-plugin`):** The `migrate` goal is run to apply SQL schema changes from `src/main/resources/db/migration`.
2.  **Code Generation (`jooq-codegen-maven`):** The `generate` goal is run to create Java source code from the database schema. This step requires the database user to have `SELECT` privileges on the `information_schema` tables.

### Configuration

Before building, you need to configure the database connection.

**1. Database Prerequisites (MySQL/MariaDB):**
```sql
CREATE DATABASE <schema> CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER '<username>'@'%' IDENTIFIED BY '<password>';
GRANT ALL ON <schema>.* TO '<username>'@'%';
FLUSH PRIVILEGES;
```
Note: On MySQL 8.0+, you cannot grant privileges on `INFORMATION_SCHEMA`; metadata read access is implicit. The grants above are sufficient for Flyway and jOOQ code generation.

**2. Application Properties:**
The default configuration is in `steve/src/main/resources/application.yml`. You can either edit this file or provide the configuration as command-line arguments during the build.

### Maven Profiles and Properties

The project uses Maven profiles defined in `pom.xml` files that can be activated with the `-P` flag:
-   `prod`: Active by default. Configured default properties for production usage (e.g., file-based logging).
-   `dev`: Configured default properties for development (e.g., console logging).
-   `useTestContainers`: Active by default. Activates the use of Testcontainers for database-related generated classes.
-   `useRealDatabase`: Uses a real database for code generation and migrations.

You can override properties using the `-D` flag. The primary properties are:
-   `db.jdbc.url`
-   `db.schema`
-   `db.user`
-   `db.password`
-   `server.port`

### Running the Build

To build the project, run:
```bash
./mvnw package
```

Example with overridden properties:
```bash
./mvnw package -Ddb.jdbc.url=<jdbcUrl> -Ddb.schema=<schema> -Ddb.user=<username> -Ddb.password=<password>
```

A runnable JAR file will be created at `steve/target/steve.war`.

## Running the Application

After building, you can run the application with the following command:
```bash
java -jar steve/target/steve.war
```

Example with overridden properties:
```bash
java -Ddb.jdbc.url=<jdbcUrl> -Ddb.schema=<schema> -Ddb.user=<username> -Ddb.password=<password> -jar steve/target/steve.war
```

## Running Tests

To run the test suite, use the following command:
```bash
./mvnw test
```

## Development Workflow

This project uses tools to enforce code style and license headers. Please adhere to the following workflow.

### Code Formatting

The project uses the `spotless-maven-plugin` to enforce a consistent code style. Before committing any changes, run the following command to format your code:
```bash
./mvnw spotless:apply
```

To check for formatting issues, you can run:
```bash
./mvnw spotless:check
```

### License Headers

All source files must include a license header. The `license-maven-plugin` is used to check for this.

To check for missing license headers, run:
```bash
./mvnw license:check
```

To automatically add license headers to your files, run:
```bash
./mvnw license:format
```
