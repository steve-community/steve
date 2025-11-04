# How to Find Where Your Ingress Certificate is Stored

## Step-by-Step Guide

### 1. Find Your Ingress Resource

```bash
# List all ingresses in your namespace
kubectl get ingress -n <your-namespace>

# Or search across all namespaces
kubectl get ingress -A | grep steve
```

### 2. Get the Ingress Details to Find the Secret Name

```bash
# Replace with your actual ingress name and namespace
kubectl get ingress <ingress-name> -n <namespace> -o yaml

# Or use jsonpath to get just the secret name
kubectl get ingress <ingress-name> -n <namespace> -o jsonpath='{.spec.tls[0].secretName}'
```

The secret name is usually something like:
- `everest-services-tls` (from your values-production.yaml)
- Or a cert-manager generated name like `steve-tls-<random>`

### 3. Check the Secret

```bash
# List secrets in your namespace
kubectl get secrets -n <namespace> | grep tls

# Get details of the TLS secret
kubectl get secret <secret-name> -n <namespace> -o yaml

# Check what keys are in the secret
kubectl get secret <secret-name> -n <namespace> -o jsonpath='{.data}' | jq 'keys'
```

### 4. Determine Certificate Source

#### Option A: If using cert-manager (you have `cert-manager.io/cluster-issuer` annotation)

```bash
# Find the Certificate resource that created the secret
kubectl get certificate -n <namespace>

# Get the certificate details
kubectl get certificate <cert-name> -n <namespace> -o yaml

# Check the issuer (CA certificate source)
kubectl get clusterissuer <issuer-name> -o yaml
# or
kubectl get issuer <issuer-name> -n <namespace> -o yaml
```

For **Let's Encrypt** (public CA): You don't need to add the CA cert - it's already in system truststores.

For **internal/self-signed CA**: You need to get the CA certificate from your issuer.

#### Option B: If manually created Secret

The certificate is directly in the Secret. Extract it using the commands below.

### 5. Extract the Certificate

#### Extract the CA Certificate (if available in secret)

```bash
# Check if there's a ca.crt in the secret
kubectl get secret <secret-name> -n <namespace> -o jsonpath='{.data.ca\.crt}' | base64 -d > ingress-ca.crt

# If ca.crt doesn't exist, the certificate chain is in tls.crt
kubectl get secret <secret-name> -n <namespace> -o jsonpath='{.data.tls\.crt}' | base64 -d > tls-chain.pem
```

#### Extract CA from Certificate Chain (if chain exists)

```bash
# Extract the server certificate
kubectl get secret <secret-name> -n <namespace> -o jsonpath='{.data.tls\.crt}' | base64 -d | openssl x509 -noout -text

# If the chain includes intermediate/root CA, extract them:
kubectl get secret <secret-name> -n <namespace> -o jsonpath='{.data.tls\.crt}' | base64 -d > full-chain.pem

# Extract just the root CA (last certificate in chain)
openssl crl2pkcs7 -nocrl -certfile full-chain.pem | openssl pkcs7 -print_certs -noout | grep -A 20 "subject=" | tail -n 20
```

#### Extract from cert-manager Certificate Resource

```bash
# If cert-manager is managing it, check the Certificate resource
kubectl get certificate <cert-name> -n <namespace> -o yaml

# The certificate might reference a ClusterIssuer/Issuer that has the CA
kubectl get clusterissuer <name> -o yaml
kubectl get issuer <name> -n <namespace> -o yaml
```

### 6. Common Scenarios

#### Scenario 1: Let's Encrypt (Public CA)
If using Let's Encrypt, **you don't need to add the CA certificate** - it's already trusted by default Java truststores.

#### Scenario 2: Self-Signed or Internal CA
If using a self-signed certificate or internal CA:

```bash
# Get the full certificate chain
kubectl get secret <secret-name> -n <namespace> -o jsonpath='{.data.tls\.crt}' | base64 -d > cert-chain.pem

# If it's a chain, extract the root CA (usually the last certificate)
# Count certificates in chain:
openssl crl2pkcs7 -nocrl -certfile cert-chain.pem | openssl pkcs7 -print_certs -noout | grep -c "subject="

# Extract root CA (last cert):
openssl crl2pkcs7 -nocrl -certfile cert-chain.pem | openssl pkcs7 -print_certs -noout | awk '/subject=/,/issuer=/' | tail -n 10 > root-ca.crt
```

#### Scenario 3: Getting CA from the Certificate Itself

If the certificate chain doesn't include the CA, you may need to:
1. Contact your certificate authority
2. Download the CA certificate from the CA's website
3. Or extract it from your certificate management system

### 7. Quick Diagnostic Commands

```bash
# Find all ingresses referencing your service
kubectl get ingress -A -o jsonpath='{range .items[*]}{.metadata.namespace}{"\t"}{.metadata.name}{"\t"}{.spec.tls[*].secretName}{"\n"}{end}' | grep -i steve

# Check if cert-manager is managing certificates
kubectl get certificate -A

# List all TLS secrets
kubectl get secrets -A | grep tls

# Check ingress annotations for cert-manager
kubectl get ingress <ingress-name> -n <namespace> -o jsonpath='{.metadata.annotations}' | jq
```

### 8. Create ConfigMap from Extracted Certificate

Once you have the CA certificate:

```bash
# Create ConfigMap from file
kubectl create configmap k8s-ingress-ca-certs \
  --from-file=ingress-ca.crt \
  --namespace=steve

# Or create from inline content
kubectl create configmap k8s-ingress-ca-certs \
  --from-literal=ingress-ca.crt="$(cat ingress-ca.crt)" \
  --namespace=steve
```

### Notes

- **If using Let's Encrypt**: No action needed - it's a public CA
- **If using self-signed**: Extract the root CA and add it to the ConfigMap
- **If using internal CA**: Get the CA certificate from your PKI/infrastructure team
- **Certificate chains**: May contain intermediate and root CA - extract the root CA for truststore

