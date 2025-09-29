# OCPP 2.0.1 Authorization Implementation

## Current Status: Experimental/Beta

The OCPP 2.0.1 authorization implementation currently **auto-accepts all authorization requests**. This is intentional for the experimental phase to facilitate testing and development.

## Implementation Details

### Auto-Accept Behavior

**Location**: `CentralSystemService20.java`

```java
public AuthorizeResponse handleAuthorize(AuthorizeRequest request, String chargeBoxId) {
    // Currently returns ACCEPTED for all requests
    IdTokenInfo idTokenInfo = new IdTokenInfo();
    idTokenInfo.setStatus(AuthorizationStatusEnum.ACCEPTED);
    // ...
}

public TransactionEventResponse handleTransactionEvent(TransactionEventRequest request, String chargeBoxId) {
    // Currently returns ACCEPTED for all transaction events
    IdTokenInfo idTokenInfo = new IdTokenInfo();
    idTokenInfo.setStatus(AuthorizationStatusEnum.ACCEPTED);
    // ...
}
```

## Production Implementation Requirements

Before moving to production, implement proper authorization logic:

### 1. ID Token Validation
- Validate token format and type (RFID, ISO14443, ISO15693, KeyCode, etc.)
- Check token against authorized user database
- Verify token expiration dates
- Handle group tokens and parent ID tokens

### 2. Authorization Cache
- Implement local authorization cache per OCPP 2.0.1 spec
- Honor cache expiry dates from `ocpp20_authorization` table
- Support offline authorization when CSMS unavailable

### 3. Contract Certificates (ISO 15118)
- Validate X.509 certificates for Plug & Charge
- Implement certificate chain verification
- Check certificate revocation lists (CRL)
- Support OCSP for certificate status

### 4. Authorization Restrictions
- Check user billing account status
- Validate charging location restrictions
- Enforce power/energy limits per user
- Support time-based restrictions

### 5. Security Considerations
- Implement rate limiting for authorization attempts
- Log all authorization attempts for audit
- Block tokens after repeated failures
- Support emergency/maintenance override tokens

## Database Schema

Authorization data is stored in `ocpp20_authorization` table:

```sql
- id_token: The RFID/token identifier
- id_token_type: Token type (RFID, ISO14443, etc.)
- status: Authorization status (Accepted/Blocked/Expired/etc.)
- cache_expiry_date: When cached authorization expires
- message_content: Message to display to user
- group_id_token: Parent group token reference
```

## Testing with Current Implementation

For testing purposes, all tokens are currently accepted. This allows:
- Testing transaction flows without token management
- Validating OCPP message exchange
- Load testing the system
- Developing UI components

## Migration Path

When implementing production authorization:

1. Create `AuthorizationService` with validation logic
2. Inject into `CentralSystemService20`
3. Replace hardcoded ACCEPTED responses
4. Add configuration flag `ocpp.v20.authorization.strict-mode`
5. Keep auto-accept mode for development/testing

## Example Production Implementation

```java
@Service
public class AuthorizationService {

    public IdTokenInfo validateToken(IdToken token, String chargeBoxId) {
        // 1. Check local cache first
        // 2. Validate token format
        // 3. Check against user database
        // 4. Apply business rules
        // 5. Return appropriate status
    }
}
```

## References

- OCPP 2.0.1 Specification Section 3.5 (Authorization)
- OCPP 2.0.1 Specification Appendix 2 (Authorization Cache)
- ISO 15118 for Plug & Charge authentication