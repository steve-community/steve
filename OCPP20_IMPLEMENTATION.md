# OCPP 2.0.1 Implementation for SteVe

## Overview

This document describes the OCPP 2.0.1 implementation for the SteVe OCPP charging station management system. The implementation provides full support for OCPP 2.0.1 Edition 2 protocol, including WebSocket communication, JSON-RPC 2.0 messaging, charge point-initiated operations, and CSMS-initiated operations.

## Table of Contents

1. [Architecture](#architecture)
2. [Configuration](#configuration)
3. [Database Schema](#database-schema)
4. [WebSocket Communication](#websocket-communication)
5. [Message Handling](#message-handling)
6. [CSMS Operations](#csms-operations)
7. [Web UI](#web-ui)
8. [Security Features](#security-features)
9. [Testing](#testing)
10. [Migration from OCPP 1.6](#migration-from-ocpp-16)

---

## Architecture

### Core Components

```
┌─────────────────────────────────────────────────────────────┐
│                      Web UI Layer                            │
│  - Ocpp20Controller (7 CSMS operations)                     │
│  - JSP pages for operation forms                             │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                   Service Layer                              │
│  - CentralSystemService20 (message handler)                 │
│  - Ocpp20TaskExecutor (async command execution)             │
│  - Ocpp20MessageDispatcher (routing)                        │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                WebSocket Layer                               │
│  - Ocpp20WebSocketHandler (connection management)           │
│  - Ocpp20WebSocketEndpoint (/ocpp/v20/{chargeBoxId})       │
│  - Rate limiting (60/min, 1000/hour per charge point)      │
│  - Basic Authentication support                              │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                Repository Layer                              │
│  - JOOQ-based persistence                                   │
│  - Type-safe database operations                             │
│  - DateTime conversion (Java 8 ↔ Joda)                      │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                   Database (MySQL)                           │
│  - OCPP 2.0 specific tables (9 tables)                      │
│  - Flyway migration V1_2_0__ocpp20_base.sql                 │
└─────────────────────────────────────────────────────────────┘
```

### Key Design Decisions

1. **JSON-RPC 2.0**: OCPP 2.0 uses JSON-RPC 2.0 over WebSocket (no SOAP support)
2. **Schema Validation**: 127 OCPP 2.0.1 + 164 OCPP 2.1 JSON schemas for type-safe message validation
3. **Async Task Execution**: CompletableFuture-based async command execution with timeout handling
4. **Database Persistence**: All operations are persisted to database for audit trail
5. **Spring Boot Conditional**: OCPP 2.0 features are enabled via `ocpp.v20.enabled=true`

---

## Configuration

### Application Properties

Add to `application.properties`:

```properties
# Enable OCPP 2.0 support
ocpp.v20.enabled=true

# WebSocket endpoint path (optional, default shown)
ocpp.v20.ws.path=/ocpp/v20

# Beta mode - restrict to specific charge points (optional)
ocpp.v20.beta.enabled=false
ocpp.v20.beta.charge-box-ids=CP001,CP002
```

### Database Configuration

OCPP 2.0 uses the same database connection as OCPP 1.x. The migration will automatically run when you start SteVe:

```sql
-- Flyway will automatically execute:
-- src/main/resources/db/migration/V1_2_0__ocpp20_base.sql
```

---

## Database Schema

### Tables Created

#### 1. `ocpp20_boot_notification`
Stores boot notification messages from charge points.

**Key Fields**:
- `id` - Primary key
- `charge_box_id` - Foreign key to charge_box
- `boot_reason` - Reason for boot (ApplicationReset, FirmwareUpdate, etc.)
- `charging_station` - JSON containing station info
- `boot_timestamp` - When the boot occurred
- `status` - CSMS response status (Accepted/Pending/Rejected)

#### 2. `ocpp20_authorization`
Caches authorization tokens with expiry.

**Key Fields**:
- `id` - Primary key
- `charge_box_id` - Foreign key to charge_box
- `id_token` - Authorization token
- `id_token_type` - Token type (ISO14443, eMAID, etc.)
- `status` - Authorization status (Accepted/Blocked/etc.)
- `cache_expiry_date_time` - When cache entry expires

#### 3. `ocpp20_transaction`
Stores transaction lifecycle information.

**Key Fields**:
- `transaction_pk` - Primary key
- `transaction_id` - OCPP transaction ID
- `charge_box_id` - Foreign key to charge_box
- `id_token` - Token that started transaction (nullable for remote starts)
- `remote_start_id` - Remote start identifier (nullable)
- `evse_id` - EVSE identifier
- `start_timestamp` - Transaction start time
- `stop_timestamp` - Transaction stop time
- `stop_reason` - Why transaction ended

#### 4. `ocpp20_transaction_event`
Event history for transactions.

**Key Fields**:
- `id` - Primary key
- `transaction_pk` - Foreign key to ocpp20_transaction
- `event_type` - Started/Updated/Ended
- `trigger_reason` - What triggered this event
- `timestamp` - Event timestamp
- `meter_value` - Energy meter reading at event time

#### 5. `ocpp20_variable`
Device model component variables.

**Key Fields**:
- `id` - Primary key
- `charge_box_id` - Foreign key to charge_box
- `component_name` - Component identifier
- `variable_name` - Variable identifier
- `instance` - Instance identifier (optional)

#### 6. `ocpp20_variable_attribute`
Variable attribute values.

**Key Fields**:
- `id` - Primary key
- `variable_id` - Foreign key to ocpp20_variable
- `attribute_type` - Actual/Target/MinSet/MaxSet
- `attribute_value` - Current value
- `mutability` - ReadOnly/WriteOnly/ReadWrite
- `last_update` - When last modified

#### 7. `ocpp20_charging_profile`
Smart charging profiles.

**Key Fields**:
- `charging_profile_pk` - Primary key
- `charge_box_id` - Foreign key to charge_box
- `charging_profile_id` - OCPP profile ID
- `stack_level` - Priority level
- `charging_profile_purpose` - Purpose of profile
- `charging_profile_kind` - Absolute/Recurring/Relative

#### 8. `ocpp20_charging_schedule`
Charging schedules within profiles.

**Key Fields**:
- `id` - Primary key
- `charging_profile_pk` - Foreign key to ocpp20_charging_profile
- `start_schedule` - When schedule starts
- `duration` - Schedule duration
- `charging_rate_unit` - W or A

#### 9. `ocpp20_charging_schedule_period`
Individual periods within schedules.

**Key Fields**:
- `id` - Primary key
- `charging_schedule_id` - Foreign key to ocpp20_charging_schedule
- `start_period` - Period start offset
- `limit` - Power/current limit
- `number_phases` - Number of phases used

---

## WebSocket Communication

### Connection Flow

```
1. Charge Point initiates WebSocket connection:
   ws://server:8080/steve/ocpp/v20/{chargeBoxId}

2. Optional Basic Authentication:
   Authorization: Basic base64(username:password)

3. Rate Limiting Check:
   - 60 requests per minute
   - 1000 requests per hour

4. Session Established:
   - Charge point is now connected
   - Can send/receive OCPP 2.0 messages
```

### Message Format (JSON-RPC 2.0)

**Call (Request)**:
```json
[
  2,
  "unique-message-id",
  "BootNotification",
  {
    "chargingStation": {
      "model": "SuperCharger",
      "vendorName": "ACME Inc."
    },
    "reason": "PowerUp"
  }
]
```

**CallResult (Response - Success)**:
```json
[
  3,
  "unique-message-id",
  {
    "currentTime": "2025-09-28T14:58:00Z",
    "interval": 300,
    "status": "Accepted"
  }
]
```

**CallError (Response - Error)**:
```json
[
  4,
  "unique-message-id",
  "InternalError",
  "Database connection failed",
  {}
]
```

---

## Message Handling

### Charge Point → CSMS Messages (22 Implemented)

#### Core Operations
- **BootNotification**: Charge point registration and status
- **Heartbeat**: Keep-alive mechanism
- **StatusNotification**: Connector/EVSE status updates
- **Authorize**: Token authorization requests

#### Transaction Management
- **TransactionEvent**: Transaction lifecycle events (Started/Updated/Ended)
- **MeterValues**: Energy consumption data

#### Device Model & Configuration
- **NotifyReport**: Device model variable reports
- **NotifyEvent**: Event notifications
- **NotifyMonitoringReport**: Monitoring data

#### Security
- **SecurityEventNotification**: Security-related events
- **SignCertificate**: Certificate signing requests

#### Firmware & Diagnostics
- **FirmwareStatusNotification**: Firmware update status
- **LogStatusNotification**: Diagnostic log status

#### Smart Charging
- **NotifyEVChargingNeeds**: EV charging requirements
- **ReportChargingProfiles**: Charging profile reports
- **NotifyChargingLimit**: Charging limit notifications
- **ClearedChargingLimit**: Cleared limits
- **NotifyEVChargingSchedule**: EV charging schedule

#### Reservations & Display
- **ReservationStatusUpdate**: Reservation status changes
- **NotifyCustomerInformation**: Customer info updates
- **NotifyDisplayMessages**: Display message updates

#### Advanced Features
- **PublishFirmwareStatusNotification**: Firmware publication status

### CSMS → Charge Point Messages (7 Implemented)

#### 1. **Reset**
Restart the charge point or specific EVSE.

**Request Parameters**:
- `type`: Immediate | OnIdle
- `evseId`: Optional EVSE to reset

**Use Cases**:
- Software update rollout
- Configuration changes
- Error recovery

#### 2. **UnlockConnector**
Unlock a connector for cable removal.

**Request Parameters**:
- `evseId`: EVSE identifier
- `connectorId`: Connector identifier

**Use Cases**:
- Emergency cable release
- Customer support requests

#### 3. **RequestStartTransaction**
Remotely start a charging session.

**Request Parameters**:
- `idToken`: Authorization token
- `remoteStartId`: Unique request identifier
- `evseId`: Optional EVSE selection
- `chargingProfile`: Optional charging profile

**Use Cases**:
- Mobile app remote start
- Scheduled charging
- Fleet management

#### 4. **RequestStopTransaction**
Remotely stop a charging session.

**Request Parameters**:
- `transactionId`: Transaction to stop

**Use Cases**:
- Time-limited charging
- Emergency stop
- Payment issues

#### 5. **GetVariables**
Query device model variables.

**Request Parameters**:
- `getVariableData[]`: Array of variables to query
  - `component`: Component identifier
  - `variable`: Variable identifier
  - `attributeType`: Optional (Actual/Target/MinSet/MaxSet)

**Use Cases**:
- Configuration audit
- Debugging
- Monitoring

#### 6. **SetVariables**
Modify device model variables.

**Request Parameters**:
- `setVariableData[]`: Array of variables to set
  - `component`: Component identifier
  - `variable`: Variable identifier
  - `attributeValue`: Value to set
  - `attributeType`: Optional (Actual/Target/MinSet/MaxSet)

**Use Cases**:
- Remote configuration
- Feature enablement
- Behavior tuning

#### 7. **TriggerMessage**
Request charge point to send a specific message.

**Request Parameters**:
- `requestedMessage`: Message type to trigger
- `evse`: Optional EVSE context
- `connectorId`: Optional connector context

**Use Cases**:
- Force status update
- Request meter values
- Diagnostic data collection

---

## CSMS Operations

### Using the Web UI

1. Navigate to **OPERATIONS → OCPP v2.0**
2. Select an operation (e.g., Reset)
3. Choose target charge points
4. Fill in required parameters
5. Click **Perform**

### Using the API (Programmatic)

```java
// Example: Remote start transaction
RequestStartTransactionTask task = new RequestStartTransactionTask(
    Arrays.asList("CP001", "CP002"),  // Charge point IDs
    "RFID-12345",                      // ID token
    12345,                             // Remote start ID
    1                                  // EVSE ID (optional)
);

Ocpp20TaskExecutor executor = context.getBean(Ocpp20TaskExecutor.class);
executor.execute(task);

// Check results
Map<String, String> results = task.getResults();
```

---

## Web UI

### Menu Structure

```
OPERATIONS
├── OCPP v1.2
├── OCPP v1.5
├── OCPP v1.6
├── OCPP v2.0 ← New!
│   ├── Reset
│   ├── UnlockConnector
│   ├── RequestStartTransaction
│   ├── RequestStopTransaction
│   ├── GetVariables
│   ├── SetVariables
│   └── TriggerMessage
└── Tasks
```

### Operation Pages

Each operation has a dedicated JSP page at:
```
/manager/operations/v2.0/{OperationName}
```

**Example**: Reset Operation
- URL: `/manager/operations/v2.0/Reset`
- Fields:
  - Charge Points: Multi-select dropdown
  - Reset Type: Immediate | OnIdle
  - EVSE ID: Optional integer
- Submit: Executes task asynchronously

---

## Security Features

### 1. Type-Safe Message Deserialization

Custom deserializer prevents injection attacks:

```java
@Override
public OcppJsonCall deserialize(JsonParser p, DeserializationContext ctxt) {
    // Validates message structure
    // Prevents malformed payloads
    // Type-safe payload extraction
}
```

### 2. WebSocket Session Management

Proper session cleanup prevents memory leaks:

```java
@Override
public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    try {
        String chargeBoxId = getChargeBoxId(session);
        sessionManager.remove(chargeBoxId);
    } finally {
        // Always cleanup even if exception occurs
    }
}
```

### 3. Basic Authentication

Charge points can authenticate using Basic Auth:

```java
@Component
public class BasicAuthValidator {
    public boolean validate(String chargeBoxId, String password) {
        // Validates against database
        // BCrypt password hashing
        // SQL injection prevention
    }
}
```

### 4. Rate Limiting

Prevents DoS attacks:

```java
@Component
public class RateLimitingService {
    // 60 requests per minute
    private static final int REQUESTS_PER_MINUTE = 60;

    // 1000 requests per hour
    private static final int REQUESTS_PER_HOUR = 1000;
}
```

---

## Testing

### Certification Test Suite

Python test suite validates OCPP 2.0.1 compliance:

```bash
# Run certification tests
python3 ocpp20_certification_test.py

# Expected output:
# ✓ BootNotification: PASS
# ✓ Heartbeat: PASS
# ✓ Authorize: PASS
# ✓ TransactionEvent (Started): PASS
# ✓ TransactionEvent (Ended): PASS
#
# All tests passed!
```

### Database Verification

Check data persistence:

```sql
-- Boot notifications
SELECT * FROM ocpp20_boot_notification
WHERE charge_box_id = 'TEST-CP-001';

-- Transactions
SELECT * FROM ocpp20_transaction
WHERE charge_box_id = 'TEST-CP-001';

-- Transaction events
SELECT e.* FROM ocpp20_transaction_event e
JOIN ocpp20_transaction t ON e.transaction_pk = t.transaction_pk
WHERE t.charge_box_id = 'TEST-CP-001';
```

### WebSocket Testing

Use `wscat` for manual testing:

```bash
# Install wscat
npm install -g wscat

# Connect to OCPP 2.0 endpoint
wscat -c ws://localhost:8080/steve/ocpp/v20/TEST-CP-001

# Send BootNotification
> [2,"msg-001","BootNotification",{"chargingStation":{"model":"Test","vendorName":"SteVe"},"reason":"PowerUp"}]

# Receive response
< [3,"msg-001",{"currentTime":"2025-09-28T14:58:00Z","interval":300,"status":"Accepted"}]
```

---

## Migration from OCPP 1.6

### Key Differences

| Feature | OCPP 1.6 | OCPP 2.0.1 |
|---------|----------|------------|
| Transport | SOAP or JSON/WebSocket | JSON/WebSocket only |
| Message Format | Operation-specific | JSON-RPC 2.0 |
| Endpoint | `/websocket/CentralSystemService/{id}` | `/ocpp/v20/{id}` |
| Configuration | Key-Value pairs | Device Model (Component/Variable) |
| Transactions | StartTransaction/StopTransaction | TransactionEvent (lifecycle) |
| Authorization | Authorize message | Authorize + cached responses |
| Smart Charging | ChargingProfile | Enhanced profiles + limits |

### Migration Steps

1. **Update Charge Point Firmware**
   - Ensure charge point supports OCPP 2.0.1
   - Configure new WebSocket URL

2. **Enable OCPP 2.0 in SteVe**
   ```properties
   ocpp.v20.enabled=true
   ```

3. **Configure Charge Point**
   - Old: `ws://server:8080/steve/websocket/CentralSystemService/CP001`
   - New: `ws://server:8080/steve/ocpp/v20/CP001`

4. **Update Authorization Cache**
   - OCPP 2.0 uses `ocpp20_authorization` table
   - Cached tokens reduce roundtrips

5. **Test Thoroughly**
   - Run certification tests
   - Verify all operations work
   - Check database persistence

### Backward Compatibility

SteVe supports both OCPP 1.6 and OCPP 2.0 simultaneously:

- OCPP 1.6 endpoints remain unchanged
- OCPP 2.0 uses separate endpoints and database tables
- Charge points can be mixed (some 1.6, some 2.0)
- UI shows both versions in OPERATIONS menu

---

## Troubleshooting

### Issue: "OCPP v2.0 menu not visible"

**Solution**:
1. Check `application.properties`: `ocpp.v20.enabled=true`
2. Restart SteVe
3. Clear browser cache
4. Verify in logs: "OCPP 2.0 Enabled: YES"

### Issue: "WebSocket connection refused"

**Solution**:
1. Check endpoint URL: `ws://server:8080/steve/ocpp/v20/{chargeBoxId}`
2. Verify charge point is registered in database
3. Check firewall allows WebSocket connections
4. Review logs for authentication failures

### Issue: "Rate limit exceeded"

**Solution**:
1. Check charge point isn't flooding with messages
2. Review rate limit settings (default: 60/min, 1000/hour)
3. Adjust limits if needed for high-traffic scenarios

### Issue: "Database migration failed"

**Solution**:
1. Check MySQL version (5.7+ required)
2. Verify database user has CREATE TABLE privileges
3. Review Flyway migration logs
4. Manually apply `V1_2_0__ocpp20_base.sql` if needed

---

## Performance Considerations

### Database Indexes

All critical foreign keys and lookup columns have indexes:

```sql
-- Sample indexes from migration
CREATE INDEX idx_ocpp20_boot_charge_box
ON ocpp20_boot_notification(charge_box_id);

CREATE INDEX idx_ocpp20_trans_charge_box
ON ocpp20_transaction(charge_box_id);

CREATE INDEX idx_ocpp20_trans_event_trans
ON ocpp20_transaction_event(transaction_pk);
```

### Connection Pooling

HikariCP connection pool is pre-configured:

```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

### Async Task Execution

CSMS operations run asynchronously:

```java
// 10 threads for parallel execution
private final Executor taskExecutor = Executors.newFixedThreadPool(10);
```

---

## Future Enhancements

### Planned Features

1. **Additional CSMS Operations**
   - ChangeAvailability
   - UpdateFirmware
   - GetLog
   - ClearCache
   - And 15+ more operations

2. **Transaction Management UI**
   - View active transactions
   - Transaction history
   - Energy consumption charts

3. **Device Model Explorer**
   - Browse all variables
   - Bulk configuration updates
   - Configuration templates

4. **Monitoring Dashboard**
   - Real-time charge point status
   - Metrics with Micrometer
   - Prometheus integration

5. **Advanced Smart Charging**
   - Load balancing
   - Dynamic pricing
   - Solar integration

---

## References

- [OCPP 2.0.1 Specification](https://www.openchargealliance.org/protocols/ocpp-201/)
- [SteVe GitHub Repository](https://github.com/steve-community/steve)
- [WebSocket RFC 6455](https://tools.ietf.org/html/rfc6455)
- [JSON-RPC 2.0 Specification](https://www.jsonrpc.org/specification)

---

## License

This implementation is part of SteVe and is licensed under GPLv3.

Copyright (C) 2013-2025 SteVe Community Team

---

**Document Version**: 1.0
**Last Updated**: 2025-09-28
**Author**: Claude (Anthropic AI) + SteVe Community