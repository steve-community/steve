#!/bin/bash
set -e

# Truststore location
TRUSTSTORE="${JAVA_HOME}/lib/security/cacerts"
TRUSTSTORE_PASSWORD="${TRUSTSTORE_PASSWORD:-changeit}"

# Sync system CA certificates to Java truststore if update-ca-certificates-java is available
if [ -x /usr/sbin/update-ca-certificates-java ]; then
    echo "Syncing system CA certificates to Java truststore..."
    /usr/sbin/update-ca-certificates-java 2>/dev/null || true
    if [ -f /etc/ssl/certs/java/cacerts ]; then
        cp /etc/ssl/certs/java/cacerts "$TRUSTSTORE"
    fi
fi

# Import certificates from mounted directory (e.g., from Kubernetes ConfigMap/Secret)
CERT_DIR="/etc/ssl/certs/k8s-ca"
if [ -d "$CERT_DIR" ]; then
    echo "Importing certificates from $CERT_DIR..."
    find "$CERT_DIR" -type f \( -name "*.crt" -o -name "*.pem" -o -name "*.cer" \) | while read -r cert_file; do
        cert_name=$(basename "$cert_file" | sed 's/[^a-zA-Z0-9]/_/g')
        echo "  Importing: $cert_file as alias 'k8s-ca-$cert_name'"
        keytool -importcert -noprompt -trustcacerts \
            -alias "k8s-ca-$cert_name" \
            -file "$cert_file" \
            -keystore "$TRUSTSTORE" \
            -storepass "$TRUSTSTORE_PASSWORD" || echo "  Warning: Failed to import $cert_file"
    done
fi

# Import certificate from legacy location (backward compatibility)
if [ -f /etc/ssl/certs/corporate-ca.pem ]; then
    echo "Importing corporate CA certificate..."
    keytool -importcert -noprompt -trustcacerts \
        -alias corporate-ca \
        -file /etc/ssl/certs/corporate-ca.pem \
        -keystore "$TRUSTSTORE" \
        -storepass "$TRUSTSTORE_PASSWORD"
fi

# Execute the main command
exec "$@"

