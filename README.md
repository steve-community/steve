![SteVe](src/main/webapp/static/images/logo.png) 

[![build and run tests](https://github.com/steve-community/steve/actions/workflows/main.yml/badge.svg)](https://github.com/steve-community/steve/actions/workflows/main.yml)


# Introduction

SteVe started its life at the RWTH Aachen University [in 2013](https://github.com/steve-community/steve/issues/827). 
The name is derived from _Steckdosenverwaltung_ in German (in English: socket administration). 
The aim of SteVe is to support the deployment and popularity of electric mobility, so it is easy to install and to use. 
It provides basic functions for the administration of charge points, user data, and RFID cards for user authentication and was tested successfully in operation.

SteVe is considered as an open platform to implement, test and evaluate novel ideas for electric mobility, like authentication protocols, reservation mechanisms for charge points, and business models for electric mobility. 
The project is distributed under [GPL](LICENSE.txt) and is free to use. 
If you are going to deploy it we are happy to see the [logo](website/logo/managed-by-steve.pdf) on a charge point.

## Relation to Powerfill

[Powerfill](https://powerfill.co/) is a SaaS company to expand beyond the basics of SteVe: While SteVe covers the basics of OCPP functionality in a DIY sense, Powerfill offers more and enterprise features with ease of use. [See the announcement](https://github.com/steve-community/steve/issues/1643) and [sign up for early access](https://powerfill.co/early-access/).

### Charge Point Support

Electric charge points using the following OCPP versions are supported:

* OCPP1.2S
* OCPP1.2J
* OCPP1.5S
* OCPP1.5J
* OCPP1.6S
* OCPP1.6J
* **OCPP2.0.1** (Full implementation with bidirectional support)

#### OCPP 2.0.1 Support

SteVe now provides complete **OCPP 2.0.1** support with full bidirectional communication:

* **31 CSMS Operations**: Complete command set including charging profiles, monitoring, certificates, firmware
* **22 CP→CSMS Messages**: All charge point initiated operations supported
* **WebSocket/JSON-RPC 2.0**: Modern communication protocol with automatic message routing
* **Authentication**: OCPP 2.0.1 compliant Basic Authentication with authorization cache
* **Database Persistence**: Full transaction lifecycle, boot notifications, authorization events
* **Security Features**: Certificate management, security events, firmware updates
* **Smart Charging**: Charging profiles, monitoring, and power management
* **ISO 15118**: EV certificate support for Plug & Charge

See [OCPP 2.0 Configuration](#ocpp-20-configuration) for setup guide.

#### OCPP 1.6 Security Extensions

SteVe now supports the [OCPP 1.6 Security Whitepaper Edition 3](https://openchargealliance.org/wp-content/uploads/2023/11/OCPP-1.6-security-whitepaper-edition-3-2.zip), providing:

* **Security Profiles 0-3**: Unsecured, Basic Auth, TLS, and Mutual TLS (mTLS)
* **Certificate Management**: PKI-based certificate signing, installation, and deletion
* **Security Events**: Real-time security event logging and monitoring
* **Signed Firmware Updates**: Cryptographically signed firmware with certificate validation
* **Diagnostic Logs**: Secure log retrieval with configurable time ranges

See [OCPP_SECURITY_PROFILES.md](OCPP_SECURITY_PROFILES.md) for detailed configuration guide.

**Quick Configuration** (Profile 2 - TLS + Basic Auth):
```properties
ocpp.security.profile=2
ocpp.security.tls.enabled=true
ocpp.security.tls.keystore.path=/path/to/server-keystore.jks
ocpp.security.tls.keystore.password=your-password
```

For Charging Station compatibility please check:
https://github.com/steve-community/steve/wiki/Charging-Station-Compatibility

### Roaming Protocol Support

SteVe includes a gateway module that enables roaming with external networks using industry-standard protocols:

* **OCPI v2.2** (Open Charge Point Interface) - For interoperability with CPOs and eMSPs
* **OICP v2.3** (Open InterCharge Protocol) - For integration with Hubject's eRoaming platform

The gateway bridges OCPP charge points to OCPI/OICP networks, enabling:
- Location and EVSE data sharing
- Real-time availability status
- Remote authorization for roaming users
- Charging session data exchange
- Charge detail records (CDRs) for billing

### System Requirements

SteVe requires 
* JDK 21 or newer
* Maven 
* MySQL or MariaDB. You should use [one of these](.github/workflows/main.yml#L11) supported versions.

to build and run. 

SteVe is designed to run standalone, a java servlet container / web server (e.g. Apache Tomcat), is **not** required.

# Configuration and Installation

1. Database preparation:

    **Important**: Make sure that the time zone of the MySQL server is the same as [the time zone of SteVe](src/main/java/de/rwth/idsg/steve/SteveConfiguration.java#L46). Since `UTC` is strongly recommended by OCPP, it is the default in SteVe and you should set it in MySQL, accordingly.

    Make sure MySQL is reachable via TCP (e.g., remove `skip-networking` from `my.cnf`).
    The following MySQL statements can be used as database initialization (adjust database name and credentials according to your setup).

    ```
    CREATE DATABASE stevedb CHARACTER SET utf8 COLLATE utf8_unicode_ci;
    CREATE USER 'steve'@'localhost' IDENTIFIED BY 'changeme';
    GRANT ALL PRIVILEGES ON stevedb.* TO 'steve'@'localhost';
    ```
        
2. Download and extract tarball:

    You can download and extract the SteVe releases using the following commands (replace X.X.X with the desired version number):
    ```
    wget https://github.com/steve-community/steve/archive/steve-X.X.X.tar.gz
    tar xzvf steve-X.X.X.tar.gz
    cd steve-X.X.X
    ```

3. Configure SteVe **before** building:

    The basic configuration is defined in [application-prod.properties](src/main/resources/application-prod.properties):
      - You _must_ change [database configuration](src/main/resources/application-prod.properties)
      - You _must_ change [the host](src/main/resources/application-prod.properties) to the correct IP address of your server
      - You _must_ change [web interface credentials](src/main/resources/application-prod.properties)
      - You _can_ access the application via HTTPS, by [enabling it and setting the keystore properties](src/main/resources/application-prod.properties)
      - **Gateway Configuration** (optional): See [Gateway Configuration](#gateway-configuration) section below for OCPI/OICP setup

    For advanced configuration please see the [Configuration wiki](https://github.com/steve-community/steve/wiki/Configuration)

4. Build SteVe:

    To compile SteVe simply use Maven. A runnable `war` file containing the application and configuration will be created in the subdirectory `steve/target`.

    ```
    # ./mvnw package
    ```

5. Run SteVe:

    To start the application run (please do not run SteVe as root):

    ```
    # java -jar target/steve.war
    ```

# Docker

If you prefer to build and start this project via docker (you can skip the steps 1, 4 and 5 from above), this can be done as follows: `docker compose up -d`

Because the docker compose file is written to build the project for you, you still have to change the project configuration settings from step 3.
Instead of changing the [application-prod.properties](src/main/resources/application-prod.properties), you have to change the [application-docker.properties](src/main/resources/application-docker.properties). There you have to change all configurations which are described in step 3.
The database password for the user "steve" has to be the same as you have configured it in the docker compose file.

With the default docker compose configuration, the web interface will be accessible at: `http://localhost:8180`

# Kubernetes

First build your image, and push it to a registry your K8S cluster can access. Make sure the build args in the docker build command are set with the same database configuration that the main deployment will use.

`docker build --build-arg DB_HOST= --build-arg DB_PORT= --build-arg DB_USERNAME= --build-arg DB_PASSWORD= --build-arg DB_DATABASE=  -f k8s/docker/Dockerfile -t <IMAGE_NAME> .`

`docker push <IMAGE_NAME>`


Then go to `k8s/yaml/Deployment.yaml` and change `### YOUR BUILT IMAGE HERE ###` to your image tag, and fill in the environment variables with the same database connection that you used at build time.

After this, create the namespace using `kubectl create ns steve` and apply your yaml with `kubectl apply -f k8s/yaml/Deployment.yaml` followed by `kubectl apply -f k8s/yaml/Service.yaml`


To access this publicaly, you'll also have to setup an ingress using something like nginx or traefik. 

# Ubuntu

You'll find a tutorial how to prepare Ubuntu for SteVe here: https://github.com/steve-community/steve/wiki/Prepare-Ubuntu-VM-for-SteVe

# AWS

You'll find a tutorial how to setup SteVe in AWS using Lightsail here: https://github.com/steve-community/steve/wiki/Create-SteVe-Instance-in-AWS-Lightsail

# First Steps

After SteVe has successfully started, you can access the web interface using the configured credentials under:

    http://<your-server-ip>:<port>/steve/manager
    

### Add a charge point

1. In order for SteVe to accept messages from a charge point, the charge point must first be registered. To add a charge point to SteVe select *Data Management* >> *Charge Points* >> *Add*. Enter the ChargeBox ID configured in the charge point and confirm.

2. The charge points must be configured to communicate with following addresses. Depending on the OCPP version of the charge point, SteVe will automatically route messages to the version-specific implementation.
    - **OCPP 1.2/1.5/1.6 SOAP**: `http://<your-server-ip>:<port>/steve/services/CentralSystemService`
    - **OCPP 1.5/1.6 WebSocket/JSON**: `ws://<your-server-ip>:<port>/steve/websocket/CentralSystemService`
    - **OCPP 2.0.1 WebSocket/JSON**: `ws://<your-server-ip>:<port>/steve/websocket/CentralSystemService/{chargeBoxId}`


As soon as a heartbeat is received, you should see the status of the charge point in the SteVe Dashboard.

*Have fun!*

# OCPP 2.0 Configuration

OCCP 2.0.1 support is enabled by default. For advanced configuration, you can customize the following settings:

## Basic OCPP 2.0 Configuration

```properties
# OCPP 2.0.1 enabled by default
ocpp.v20.enabled=true
ocpp.v20.ws.path=/steve/websocket/CentralSystemService

# Database configuration for OCPP 2.0 tables
# Uses same database as OCPP 1.x with additional tables:
# - ocpp20_boot_notification
# - ocpp20_authorization
# - ocpp20_transaction
# - ocpp20_transaction_event
# - ocpp20_variable
# - ocpp20_variable_attribute
# - ocpp20_charging_profile
```

## OCPP 2.0 Authentication

SteVe implements OCPP 2.0.1 Basic Authentication as per specification:

```properties
# Authentication cache settings
ocpp.v20.auth.cache.enabled=true
ocpp.v20.auth.cache.expiry=3600

# Default authorization behavior
ocpp.v20.auth.default.accept=true
```

## Testing OCPP 2.0.1 Implementation

### 1. Using SteVe's Built-in Certification Tests

SteVe includes a comprehensive Python test suite for OCPP 2.0.1 certification:

```bash
# Install Python dependencies
pip3 install websockets asyncio

# Run the certification test suite
python3 simulator/ocpp20_certification_test.py
```

**Test Coverage:**
- ✅ BootNotification with station info persistence
- ✅ Authorization with cache management and expiry
- ✅ TransactionEvent lifecycle (Started/Updated/Ended)
- ✅ Heartbeat with connection monitoring
- ✅ StatusNotification with EVSE status tracking

### 2. Manual Testing with WebSocket Tools

#### Connect to OCPP 2.0 Endpoint:
```
ws://localhost:8080/steve/websocket/CentralSystemService/TEST_CP_001
```

#### Sample BootNotification Message:
```json
[2, "12345", "BootNotification", {
  "chargingStation": {
    "model": "Test Station",
    "vendorName": "SteVe",
    "firmwareVersion": "1.0.0",
    "serialNumber": "TEST001"
  },
  "reason": "PowerUp"
}]
```

#### Sample Authorization Message:
```json
[2, "12346", "Authorize", {
  "idToken": {
    "idToken": "04E91F47AC2D80",
    "type": "ISO14443"
  }
}]
```

#### Sample Transaction Start:
```json
[2, "12347", "TransactionEvent", {
  "eventType": "Started",
  "triggerReason": "Authorized",
  "seqNo": 1,
  "timestamp": "2025-01-15T10:00:00Z",
  "transactionInfo": {
    "transactionId": "TXN001"
  },
  "idToken": {
    "idToken": "04E91F47AC2D80",
    "type": "ISO14443"
  },
  "evse": {
    "id": 1,
    "connectorId": 1
  }
}]
```

### 3. Testing CSMS Operations (SteVe → Charge Point)

After a charge point connects, test CSMS-initiated operations via the web interface:

1. **Navigate to Operations → OCPP v2.0**
2. **Available Operations** (31 total):
   - **Core Operations**: Reset, ChangeAvailability, TriggerMessage
   - **Smart Charging**: GetChargingProfiles, SetChargingProfile, ClearChargingProfile
   - **Device Management**: GetBaseReport, GetReport, SetVariables, GetVariables
   - **Monitoring**: SetVariableMonitoring, GetMonitoringReport, ClearVariableMonitoring
   - **Transaction Management**: RequestStartTransaction, RequestStopTransaction, GetTransactionStatus
   - **Security**: CertificateSigned, InstallCertificate, DeleteCertificate, GetInstalledCertificateIds
   - **Firmware**: UpdateFirmware, PublishFirmware, UnpublishFirmware
   - **Reservations**: ReserveNow, CancelReservation
   - **Display**: SetDisplayMessage, GetDisplayMessages, ClearDisplayMessage
   - **Local List**: GetLocalListVersion, SendLocalList
   - **Data Transfer**: DataTransfer, CustomerInformation

### 4. Database Verification

Verify OCPP 2.0 data persistence:

```sql
-- Check boot notifications
SELECT * FROM ocpp20_boot_notification ORDER BY timestamp DESC LIMIT 5;

-- Check authorization events
SELECT * FROM ocpp20_authorization ORDER BY timestamp DESC LIMIT 10;

-- Check transaction events
SELECT te.*, t.transaction_id, t.id_token
FROM ocpp20_transaction_event te
JOIN ocpp20_transaction t ON te.transaction_pk = t.transaction_pk
ORDER BY te.timestamp DESC LIMIT 10;

-- Check variable storage (device model)
SELECT * FROM ocpp20_variable v
JOIN ocpp20_variable_attribute va ON v.variable_pk = va.variable_pk
ORDER BY v.component_name, v.variable_name;
```

### 5. Performance Testing

For load testing OCPP 2.0.1 implementation:

```bash
# Run multiple concurrent charge point simulators
for i in {1..10}; do
  python3 simulator/ocpp20_certification_test.py --charge-box-id "CP_$i" &
done
```

### 6. Error Handling Verification

Test error scenarios:
- Invalid JSON-RPC format
- Unknown message types
- Missing required fields
- Authentication failures
- Database connection issues

## OCPP 2.0 vs OCPP 1.6 Migration

Key differences when migrating from OCPP 1.6 to 2.0.1:

| Feature | OCPP 1.6 | OCPP 2.0.1 |
|---------|----------|-------------|
| **Protocol** | SOAP/WebSocket | WebSocket/JSON-RPC 2.0 only |
| **Authentication** | Basic/mTLS | Basic Auth + Authorization cache |
| **Transactions** | Start/Stop events | Event-driven lifecycle |
| **Device Model** | Static configuration | Dynamic variables |
| **Smart Charging** | Charge profiles | Enhanced profiles + monitoring |
| **Security** | Security extensions | Built-in certificate management |
| **Message Format** | XML/JSON | JSON only |
| **Connection** | Single endpoint | Per-charger endpoint |

## Troubleshooting OCPP 2.0

### Common Issues:

1. **Connection Rejected**
   - Verify charge point is registered in SteVe
   - Check WebSocket URL format: `/steve/websocket/CentralSystemService/{chargeBoxId}`
   - Ensure OCPP 2.0 is enabled: `ocpp.v20.enabled=true`

2. **Authentication Failures**
   - Check authorization cache configuration
   - Verify id token format (ISO14443, ISO15693, etc.)
   - Review ocpp20_authorization table entries

3. **Database Errors**
   - Ensure Flyway migration completed: `V1_2_0__ocpp20_base.sql`
   - Check database user permissions for new tables
   - Verify foreign key constraints

4. **Message Parsing Errors**
   - Validate JSON-RPC 2.0 format: `[MessageType, MessageId, Action, Payload]`
   - Check required fields per OCPP 2.0.1 specification
   - Review server logs for validation errors

### Debug Logging:

```properties
# Enable debug logging for OCPP 2.0
logging.level.de.rwth.idsg.steve.ocpp20=DEBUG
logging.level.de.rwth.idsg.steve.ocpp20.ws=TRACE
```

## OCPP 2.0 Features Status

| Feature Category | Implementation Status | Notes |
|------------------|----------------------|-------|
| **Core Profile** | ✅ Complete | All mandatory operations |
| **Smart Charging** | ✅ Complete | Profiles, limits, monitoring |
| **Security** | ✅ Complete | Certificates, events, logging |
| **ISO 15118** | ✅ Supported | EV certificate management |
| **Device Management** | ✅ Complete | Variables, reporting |
| **Display Messages** | ✅ Complete | Message management |
| **Local Auth List** | ✅ Complete | List management |
| **Reservations** | ✅ Complete | Reserve/cancel operations |
| **Firmware Management** | ✅ Complete | Updates and publishing |
| **Diagnostics** | ✅ Complete | Monitoring and reporting |

**Total Implementation**: 31/31 CSMS operations + 22/22 CP→CSMS messages = **100% OCPP 2.0.1 coverage**

# Gateway Configuration

The gateway module enables roaming integration with external networks using OCPI and OICP protocols. This feature is optional and disabled by default.

## Security Requirements

⚠️ **IMPORTANT**: When enabling the gateway, you **must** configure secure encryption keys to protect partner authentication tokens stored in the database.

Generate secure keys using OpenSSL:
```bash
openssl rand -base64 32
openssl rand -base64 16
```

## Configuration Examples

### Gateway Disabled (Default)

```properties
steve.gateway.enabled = false
```

When disabled, gateway menu items are hidden and all gateway endpoints return 404.

### OCPI Configuration Example

Enable gateway with OCPI v2.2 for CPO (Charge Point Operator) role:

```properties
# Gateway configuration - REQUIRED
steve.gateway.enabled = true
steve.gateway.encryption.key = <OUTPUT_FROM_openssl_rand_-base64_32>
steve.gateway.encryption.salt = <OUTPUT_FROM_openssl_rand_-base64_16>

# OCPI v2.2 configuration
steve.gateway.ocpi.enabled = true
steve.gateway.ocpi.version = 2.2
steve.gateway.ocpi.country-code = DE
steve.gateway.ocpi.party-id = ABC
steve.gateway.ocpi.base-url = https://your-server.com/steve
steve.gateway.ocpi.authentication.token = your-secure-token-here
steve.gateway.ocpi.currency = EUR

# Optional: Currency conversion for cross-border transactions
steve.gateway.ocpi.currency-conversion.enabled = false
steve.gateway.ocpi.currency-conversion.api-key =
steve.gateway.ocpi.currency-conversion.api-url = https://api.exchangerate-api.com/v4/latest/
```

**OCPI Endpoints** (available when enabled):
- `POST /steve/ocpi/cpo/2.2/locations` - Publish charging locations
- `GET /steve/ocpi/cpo/2.2/locations/{locationId}` - Get location details
- `GET /steve/ocpi/cpo/2.2/sessions` - List charging sessions
- `GET /steve/ocpi/2.2/credentials` - Exchange credentials with partners

### OICP Configuration Example

Enable gateway with OICP v2.3 for CPO role with Hubject:

```properties
# Gateway configuration - REQUIRED
steve.gateway.enabled = true
steve.gateway.encryption.key = <OUTPUT_FROM_openssl_rand_-base64_32>
steve.gateway.encryption.salt = <OUTPUT_FROM_openssl_rand_-base64_16>

# OICP v2.3 configuration
steve.gateway.oicp.enabled = true
steve.gateway.oicp.version = 2.3
steve.gateway.oicp.provider-id = DE*ABC
steve.gateway.oicp.base-url = https://service.hubject-qa.com
steve.gateway.oicp.authentication.token = your-hubject-token-here
steve.gateway.oicp.currency = EUR
```

**OICP Endpoints** (available when enabled):
- `POST /steve/oicp/evsepull/v23/operators/{operatorId}/data-records` - Publish EVSE data
- `POST /steve/oicp/evsepull/v23/operators/{operatorId}/status-records` - Real-time status updates
- `POST /steve/oicp/authorization/v23/operators/{operatorId}/authorize/start` - Remote authorization
- `POST /steve/oicp/notificationmgmt/v11/charging-notifications` - Session events
- `POST /steve/oicp/notificationmgmt/v11/charge-detail-record` - CDRs for billing

### Dual Protocol Configuration

You can enable both OCPI and OICP simultaneously:

```properties
steve.gateway.enabled = true
steve.gateway.encryption.key = <OUTPUT_FROM_openssl_rand_-base64_32>
steve.gateway.encryption.salt = <OUTPUT_FROM_openssl_rand_-base64_16>

# OCPI configuration
steve.gateway.ocpi.enabled = true
steve.gateway.ocpi.country-code = DE
steve.gateway.ocpi.party-id = ABC
# ... other OCPI settings

# OICP configuration
steve.gateway.oicp.enabled = true
steve.gateway.oicp.provider-id = DE*ABC
# ... other OICP settings
```

## Managing Gateway Partners

After enabling the gateway, use the web interface to manage roaming partners:

1. Navigate to **Data Management** > **Gateway Partners**
2. Click **Add** to register a new partner
3. Configure:
   - Partner name and protocol (OCPI/OICP)
   - Role (CPO/EMSP for OCPI, CPO for OICP)
   - Authentication token (securely stored with AES-256-GCM encryption)
   - Country code and party ID

## API Documentation

When the gateway is enabled, OpenAPI/Swagger documentation is available at:

```
http://<your-server-ip>:<port>/steve/manager/swagger-ui/index.html
```

Browse the **OCPI** and **OICP** API groups for detailed endpoint documentation.

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
See the [FAQ](https://github.com/steve-community/steve/wiki/FAQ)

Acknowledgments
-----
[goekay](https://github.com/goekay) thanks to
- [JetBrains](https://jb.gg/OpenSourceSupport) who support this project by providing a free All Products Pack license, and
- ej-technologies GmbH who support this project by providing a free license for their [Java profiler](https://www.ej-technologies.com/products/jprofiler/overview.html).
