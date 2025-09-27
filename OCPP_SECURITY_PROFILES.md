# OCPP 1.6 Security Profiles Configuration Guide

This document describes how to configure SteVe to support the three OCPP 1.6 security profiles defined in the OCPP 1.6 Security Whitepaper Edition 3.

---

## Security Profile Overview

### Profile 0: Unsecured Transport with Basic Authentication
- **Transport**: HTTP or WebSocket (ws://)
- **Authentication**: HTTP Basic Authentication
- **Encryption**: None
- **Use Case**: Development, testing, closed networks

### Profile 1: Unsecured Transport with Basic Authentication
- **Transport**: HTTP or WebSocket (ws://)
- **Authentication**: HTTP Basic Authentication + Charge Point Password
- **Encryption**: None
- **Use Case**: Private networks with additional authentication layer

### Profile 2: TLS with Basic Authentication
- **Transport**: HTTPS or Secure WebSocket (wss://)
- **Authentication**: HTTP Basic Authentication + TLS Server Certificate
- **Encryption**: TLS 1.2 or higher
- **Use Case**: Production environments with server authentication

### Profile 3: TLS with Client-Side Certificates
- **Transport**: HTTPS or Secure WebSocket (wss://)
- **Authentication**: Mutual TLS (mTLS) with client certificates
- **Encryption**: TLS 1.2 or higher
- **Use Case**: High-security production environments

---

## Configuration Properties

Add these properties to `application-prod.properties` or `application-test.properties`:

```properties
# OCPP Security Profile (0, 1, 2, or 3)
ocpp.security.profile=2

# TLS Configuration (required for Profile 2 and 3)
ocpp.security.tls.enabled=true

# Server Keystore (contains server certificate and private key)
ocpp.security.tls.keystore.path=/path/to/server-keystore.jks
ocpp.security.tls.keystore.password=your-keystore-password
ocpp.security.tls.keystore.type=JKS

# Truststore (contains trusted CA certificates)
ocpp.security.tls.truststore.path=/path/to/truststore.jks
ocpp.security.tls.truststore.password=your-truststore-password
ocpp.security.tls.truststore.type=JKS

# Client Certificate Authentication (required for Profile 3)
ocpp.security.tls.client.auth=false

# TLS Protocol Versions (comma-separated)
ocpp.security.tls.protocols=TLSv1.2,TLSv1.3

# TLS Cipher Suites (optional, leave empty for defaults)
ocpp.security.tls.ciphers=
```

---

## Profile 0 Configuration (Unsecured)

**⚠️ NOT RECOMMENDED FOR PRODUCTION**

```properties
ocpp.security.profile=0
ocpp.security.tls.enabled=false

# Use HTTP Basic Auth credentials
auth.user=admin
auth.password=your-password
```

**WebSocket URL**: `ws://your-server:8080/steve/websocket/CentralSystemService/{chargePointId}`

---

## Profile 1 Configuration (Basic Auth Only)

**⚠️ NOT RECOMMENDED FOR PRODUCTION**

```properties
ocpp.security.profile=1
ocpp.security.tls.enabled=false

# Configure charge point authorization keys in database
# Each charge point should have an authorization_key set
```

**WebSocket URL**: `ws://your-server:8080/steve/websocket/CentralSystemService/{chargePointId}`

**Database**: Set `authorization_key` column in `charge_box` table for each charge point.

---

## Profile 2 Configuration (TLS + Basic Auth)

**✅ RECOMMENDED FOR PRODUCTION**

### Step 1: Generate Server Certificate

```bash
# Create server keystore with self-signed certificate (for testing)
keytool -genkeypair -alias steve-server \
  -keyalg RSA -keysize 2048 -validity 365 \
  -keystore server-keystore.jks \
  -storepass changeit \
  -dname "CN=steve.example.com, OU=SteVe, O=Example, L=City, ST=State, C=US"

# OR: Import existing certificate and private key
# (Use openssl to convert PEM to PKCS12, then import to JKS)
```

### Step 2: Configure Properties

```properties
ocpp.security.profile=2
ocpp.security.tls.enabled=true

# Server certificate
ocpp.security.tls.keystore.path=/opt/steve/certs/server-keystore.jks
ocpp.security.tls.keystore.password=changeit
ocpp.security.tls.keystore.type=JKS

# Enable HTTPS on Jetty
https.enabled=true
https.port=8443
keystore.path=/opt/steve/certs/server-keystore.jks
keystore.password=changeit

# Client authentication NOT required for Profile 2
ocpp.security.tls.client.auth=false
```

### Step 3: Configure Charge Points

**WebSocket URL**: `wss://steve.example.com:8443/steve/websocket/CentralSystemService/{chargePointId}`

**Certificate**: Charge points must trust the server certificate. Install the CA certificate or server certificate on charge points.

---

## Profile 3 Configuration (Mutual TLS)

**✅ RECOMMENDED FOR HIGH-SECURITY ENVIRONMENTS**

### Step 1: Generate CA Certificate

```bash
# Create CA private key and certificate
openssl genrsa -out ca-key.pem 4096
openssl req -new -x509 -days 3650 -key ca-key.pem -out ca-cert.pem \
  -subj "/CN=SteVe CA/O=Example/C=US"
```

### Step 2: Generate Server Certificate (Signed by CA)

```bash
# Generate server private key and CSR
openssl genrsa -out server-key.pem 2048
openssl req -new -key server-key.pem -out server.csr \
  -subj "/CN=steve.example.com/O=Example/C=US"

# Sign server certificate with CA
openssl x509 -req -in server.csr -CA ca-cert.pem -CAkey ca-key.pem \
  -CAcreateserial -out server-cert.pem -days 365

# Convert to PKCS12
openssl pkcs12 -export -in server-cert.pem -inkey server-key.pem \
  -out server.p12 -name steve-server -passout pass:changeit

# Import to JKS keystore
keytool -importkeystore -srckeystore server.p12 -srcstoretype PKCS12 \
  -destkeystore server-keystore.jks -deststoretype JKS \
  -srcstorepass changeit -deststorepass changeit
```

### Step 3: Create Truststore with CA Certificate

```bash
# Import CA certificate to truststore
keytool -import -trustcacerts -alias ca-cert \
  -file ca-cert.pem -keystore truststore.jks \
  -storepass changeit -noprompt
```

### Step 4: Generate Client Certificates (for each Charge Point)

```bash
# Generate client private key and CSR
openssl genrsa -out client-cp001-key.pem 2048
openssl req -new -key client-cp001-key.pem -out client-cp001.csr \
  -subj "/CN=CP001/O=Example/C=US"

# Sign client certificate with CA
openssl x509 -req -in client-cp001.csr -CA ca-cert.pem -CAkey ca-key.pem \
  -CAcreateserial -out client-cp001-cert.pem -days 365

# Convert to PKCS12 for charge point
openssl pkcs12 -export -in client-cp001-cert.pem -inkey client-cp001-key.pem \
  -out client-cp001.p12 -name cp001 -passout pass:changeit
```

### Step 5: Configure Properties

```properties
ocpp.security.profile=3
ocpp.security.tls.enabled=true

# Server certificate
ocpp.security.tls.keystore.path=/opt/steve/certs/server-keystore.jks
ocpp.security.tls.keystore.password=changeit
ocpp.security.tls.keystore.type=JKS

# Truststore with CA certificate (to verify client certificates)
ocpp.security.tls.truststore.path=/opt/steve/certs/truststore.jks
ocpp.security.tls.truststore.password=changeit
ocpp.security.tls.truststore.type=JKS

# Require client certificates
ocpp.security.tls.client.auth=true

# TLS protocols
ocpp.security.tls.protocols=TLSv1.2,TLSv1.3

# Enable HTTPS
https.enabled=true
https.port=8443
keystore.path=/opt/steve/certs/server-keystore.jks
keystore.password=changeit
```

### Step 6: Install Client Certificates on Charge Points

1. Transfer `client-cp001.p12` to charge point CP001
2. Configure charge point to use client certificate for mTLS
3. Configure charge point with CA certificate to verify server
4. Set WebSocket URL: `wss://steve.example.com:8443/steve/websocket/CentralSystemService/CP001`

---

## Security Best Practices

### Certificate Management

1. **Use a proper CA**: For production, use certificates from a trusted CA (Let's Encrypt, DigiCert, etc.)
2. **Certificate rotation**: Renew certificates before expiry
3. **Revocation**: Implement CRL or OCSP for certificate revocation
4. **Key length**: Use at least 2048-bit RSA keys or 256-bit ECC keys
5. **Storage**: Protect private keys with strong passwords and secure storage

### TLS Configuration

1. **Protocol versions**: Use TLS 1.2 or higher, disable SSLv3 and TLS 1.0/1.1
2. **Cipher suites**: Use strong ciphers (AES-GCM, ChaCha20-Poly1305)
3. **Perfect Forward Secrecy**: Prefer ECDHE or DHE cipher suites
4. **HSTS**: Enable HTTP Strict Transport Security

### Recommended Cipher Suites

```properties
ocpp.security.tls.ciphers=\
  TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,\
  TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,\
  TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256,\
  TLS_DHE_RSA_WITH_AES_256_GCM_SHA384,\
  TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
```

---

## Database Configuration

The `charge_box` table includes security-related columns:

```sql
-- Security profile for this charge point (0-3)
ALTER TABLE charge_box ADD COLUMN security_profile INT DEFAULT 0;

-- Authorization key for Profile 1+ (optional)
ALTER TABLE charge_box ADD COLUMN authorization_key VARCHAR(100);

-- CPO name (for certificate validation)
ALTER TABLE charge_box ADD COLUMN cpo_name VARCHAR(255);

-- Certificate store max length
ALTER TABLE charge_box ADD COLUMN certificate_store_max_length INT;

-- Additional root certificate check
ALTER TABLE charge_box ADD COLUMN additional_root_certificate_check BOOLEAN DEFAULT FALSE;
```

---

## Troubleshooting

### Connection Fails with "SSL Handshake Error"

- **Check**: Certificate validity (not expired)
- **Check**: Hostname matches CN in server certificate
- **Check**: Charge point trusts the server certificate or CA

### Client Certificate Not Accepted

- **Check**: Client certificate signed by trusted CA in truststore
- **Check**: Client certificate not expired
- **Check**: `ocpp.security.tls.client.auth=true` is set

### TLS Version Mismatch

- **Check**: Both server and charge point support same TLS version
- **Check**: `ocpp.security.tls.protocols` includes supported versions

### Certificate Validation Fails

- **Check**: CN in certificate matches charge point ID or hostname
- **Check**: Certificate chain is complete
- **Check**: CA certificate imported to truststore

---

## Testing TLS Configuration

### Test Server Certificate with OpenSSL

```bash
# Test TLS connection
openssl s_client -connect steve.example.com:8443 -showcerts

# Test with client certificate
openssl s_client -connect steve.example.com:8443 \
  -cert client-cp001-cert.pem -key client-cp001-key.pem
```

### Test WebSocket Connection

```bash
# Install wscat: npm install -g wscat

# Test Profile 2 (wss://)
wscat -c "wss://steve.example.com:8443/steve/websocket/CentralSystemService/CP001"

# Test Profile 3 (wss:// with client cert)
wscat -c "wss://steve.example.com:8443/steve/websocket/CentralSystemService/CP001" \
  --cert client-cp001.p12 --passphrase changeit
```

---

## References

- [OCPP 1.6 Security Whitepaper Edition 3](https://openchargealliance.org/protocols/open-charge-point-protocol/)
- [Java Keytool Documentation](https://docs.oracle.com/en/java/javase/17/docs/specs/man/keytool.html)
- [OpenSSL Documentation](https://www.openssl.org/docs/)
- [Spring Boot SSL Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#application-properties.server)

---

## Support

For questions or issues:
- GitHub: https://github.com/steve-community/steve/issues
- OCPP Forum: https://openchargealliance.org/

---

**Last Updated**: 2025-09-27
**SteVe Version**: 3.x with OCPP 1.6 Security Extensions