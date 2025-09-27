# OCPI/OICP Gateway Layer

This gateway layer enables Steve to communicate with other charging networks and platforms using industry-standard roaming protocols: OCPI (Open Charge Point Interface) and OICP (Open InterCharge Protocol).

## Overview

The gateway layer acts as a bridge between Steve's OCPP-based charge point management and external roaming networks:

- **OCPI v2.2**: Enables peer-to-peer roaming between CPOs (Charge Point Operators) and EMSPs (E-Mobility Service Providers)
- **OICP v2.3**: Enables roaming through Hubject's intercharge network

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                         Steve OCPP                           │
│  (Charge Points communicate via OCPP 1.5/1.6)               │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    Protocol Adapters                         │
│  • OcppToOcpiAdapter - Converts OCPP to OCPI format        │
│  • OcppToOicpAdapter - Converts OCPP to OICP format        │
└──────────────┬─────────────────────┬────────────────────────┘
               │                     │
        ┌──────▼──────┐      ┌──────▼──────┐
        │ OCPI Layer  │      │ OICP Layer  │
        │  • CPO API  │      │  • CPO API  │
        │  • EMSP API │      │  • Auth API │
        └─────────────┘      └─────────────┘
```

## Features

### OCPI Implementation (v2.2)

#### CPO (Charge Point Operator) Endpoints:
- `GET /ocpi/cpo/2.2/locations` - Publish charge point locations
- `GET /ocpi/cpo/2.2/sessions` - Provide active charging sessions
- `GET /ocpi/cpo/2.2/cdrs` - Provide charge detail records (CDRs)

#### EMSP (E-Mobility Service Provider) Endpoints:
- `POST /ocpi/emsp/2.2/tokens` - Authorize RFID tokens from partner networks

#### Version Discovery:
- `GET /ocpi/versions` - List supported OCPI versions
- `GET /ocpi/2.2` - Get v2.2 endpoint details

### OICP Implementation (v2.3)

#### Provider Endpoints:
- `GET /oicp/2.3/evse-data` - Publish EVSE (charging station) data
- `POST /oicp/2.3/authorization/start` - Authorize charging session start
- `POST /oicp/2.3/authorization/stop` - Authorize charging session stop
- `POST /oicp/2.3/charging-notifications` - Receive charging notifications
- `POST /oicp/2.3/charge-detail-records` - Receive charge detail records

## Configuration

### 1. Enable Gateway Layer

Edit `application-prod.properties`:

```properties
# Enable gateway functionality
steve.gateway.enabled = true
```

### 2. Configure OCPI

```properties
# Enable OCPI protocol
steve.gateway.ocpi.enabled = true
steve.gateway.ocpi.version = 2.2

# Your party identification (ISO-3166 alpha-2 country code + 3-char party ID)
steve.gateway.ocpi.country-code = DE
steve.gateway.ocpi.party-id = ABC

# Your public OCPI endpoint URL
steve.gateway.ocpi.base-url = https://your-domain.com/ocpi

# Authentication token for incoming requests
steve.gateway.ocpi.authentication.token = your-secret-token-here
```

### 3. Configure OICP

```properties
# Enable OICP protocol
steve.gateway.oicp.enabled = true
steve.gateway.oicp.version = 2.3

# Your Hubject provider ID
steve.gateway.oicp.provider-id = DE*ABC

# Your public OICP endpoint URL
steve.gateway.oicp.base-url = https://your-domain.com/oicp

# Authentication token for incoming requests
steve.gateway.oicp.authentication.token = your-secret-token-here
```

## Database Schema

The gateway layer adds five new tables:

### gateway_config
Stores protocol configuration and synchronization status.

### gateway_partner
Stores information about connected roaming partners (other CPOs/EMSPs).

### gateway_session_mapping
Maps Steve transaction IDs to external session IDs (OCPI/OICP).

### gateway_cdr_mapping
Maps Steve transactions to sent CDRs (Charge Detail Records).

### gateway_token_mapping
Maps Steve OCPP tags to external token UIDs.

## API Endpoints

### OCPI Endpoints

All OCPI endpoints follow the specification at: https://github.com/ocpi/ocpi

#### CPO Role (You as operator):
```
GET    /ocpi/cpo/2.2/locations
       ?offset=0&limit=50
       → Returns charge point locations

GET    /ocpi/cpo/2.2/sessions
       ?offset=0&limit=50&date_from=2025-01-01T00:00:00Z
       → Returns charging sessions

GET    /ocpi/cpo/2.2/cdrs
       ?offset=0&limit=50&date_from=2025-01-01T00:00:00Z
       → Returns charge detail records
```

#### EMSP Role (Token authorization):
```
POST   /ocpi/emsp/2.2/tokens/{token_uid}/authorize
       ?type=RFID
       {
         "location_id": "LOC1"
       }
       → Authorizes a token for charging
```

### OICP Endpoints

All OICP endpoints follow the Hubject OICP specification.

```
GET    /oicp/2.3/evse-data
       → Returns EVSE data

POST   /oicp/2.3/authorization/start
       {
         "SessionID": "...",
         "Identification": {...}
       }
       → Authorizes session start

POST   /oicp/2.3/authorization/stop
       {
         "SessionID": "..."
       }
       → Authorizes session stop

POST   /oicp/2.3/charging-notifications
       {
         "Type": "Start",
         "SessionID": "..."
       }
       → Receives charging notifications
```

## Security

### Authentication

Both OCPI and OICP use token-based authentication:

- **OCPI**: Uses `Authorization: Token <your-token>` header
- **OICP**: Uses custom authentication headers

Configure tokens in `application-prod.properties` or via the web interface.

### HTTPS

**Important**: Always use HTTPS in production. Both OCPI and OICP require encrypted communication.

Configure HTTPS in `application-prod.properties`:

```properties
https.enabled = true
https.port = 8443
keystore.path = /path/to/keystore.jks
keystore.password = your-keystore-password
```

## Integration Examples

### Partner Setup

1. **Add a roaming partner** via Steve web interface:
   - Go to "Gateway" → "Partners" → "Add Partner"
   - Enter partner details (name, protocol, endpoint URL, credentials)
   - Enable the partner

2. **Exchange credentials**:
   - Share your endpoint URL and token with the partner
   - Store partner's endpoint URL and token in Steve

3. **Test connectivity**:
   - Use the "Test Connection" button in the partner details
   - Check logs for successful handshake

### OCPI Peer-to-Peer Flow

```
Your Network (CPO)              Partner Network (EMSP)
    │                                    │
    │  1. Publish locations              │
    │  GET /locations ────────────────>  │
    │                                    │
    │  2. Driver arrives, uses RFID      │
    │  <────────────────────────────────  │
    │  POST /tokens/authorize            │
    │                                    │
    │  3. Start charging session         │
    │  POST /sessions ───────────────────>│
    │                                    │
    │  4. Send CDR when completed        │
    │  POST /cdrs ───────────────────────>│
```

### OICP Hub Flow (Hubject)

```
Your Network              Hubject Hub              Partner Network
    │                         │                         │
    │  1. Push EVSE data      │                         │
    │  ────────────────────>  │                         │
    │                         │                         │
    │  2. Authorization req   │  3. Forward to partner  │
    │  <────────────────────  │  ────────────────────>  │
    │                         │                         │
    │  4. Charging notif      │  5. Forward to Hubject  │
    │  ────────────────────>  │  ────────────────────>  │
```

## Protocol Adapters

The protocol adapters handle conversion between OCPP and roaming protocols:

### OcppToOcpiAdapter

Converts Steve's internal OCPP data to OCPI format:

- **Charge Points** → **Locations** with EVSEs and Connectors
- **Transactions** → **Sessions** with charging periods
- **Completed Transactions** → **CDRs** with pricing
- **OCPP Tags** → **Tokens** for authorization

### OcppToOicpAdapter

Converts Steve's internal OCPP data to OICP format:

- **Charge Points** → **EVSE Data** records
- **Authorization Requests** → **Authorization Start/Stop**
- **Transactions** → **Charging Notifications**
- **Completed Transactions** → **Charge Detail Records**

## Troubleshooting

### Enable Debug Logging

Add to `logback-spring-prod.xml`:

```xml
<logger name="de.rwth.idsg.steve.gateway" level="DEBUG"/>
```

### Common Issues

**Issue**: "Protocol not enabled"
- **Solution**: Set `steve.gateway.enabled = true` and restart

**Issue**: "Authentication failed"
- **Solution**: Verify token configuration matches partner's expectations

**Issue**: "Location/EVSE not found"
- **Solution**: Ensure charge points are properly registered in Steve

**Issue**: "Token authorization fails"
- **Solution**: Check OCPP tag is valid and active in Steve database

## Standards Compliance

This implementation follows:

- **OCPI 2.2**: https://github.com/ocpi/ocpi/tree/2.2
- **OICP 2.3**: https://github.com/hubject/oicp

## Future Enhancements

Planned features:

- [ ] OCPI 2.2.1 support
- [ ] OICP 2.3.1 support
- [ ] Smart charging via OCPI
- [ ] Tariff management
- [ ] Reservation support
- [ ] WebSocket push notifications
- [ ] Hub/roaming platform integration
- [ ] Multi-tenant support

## Support

For issues or questions:
- GitHub: https://github.com/steve-community/steve/issues
- Documentation: https://github.com/steve-community/steve/wiki